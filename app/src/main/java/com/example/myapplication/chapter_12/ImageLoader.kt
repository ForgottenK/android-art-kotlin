package com.example.myapplication.chapter_12

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import androidx.core.util.lruCache
import com.example.myapplication.R
import com.example.myapplication.utils.MyUtils
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 *    @author wangruixiang
 *    @date 2021/4/27 12:43 AM
 */
class ImageLoader(context: Context) {
    companion object {
        private const val TAG = "wangruixiang"

        const val MESSAGE_POST_RESULT = 1
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val CORE_POOL_SIZE = CPU_COUNT + 1
        private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1
        private const val KEEP_ALIVE = 10L

        private const val TAG_KEY_URI = R.id.imageloader_uri
        private const val DISK_CACHE_SIZE: Long = 1024 * 1024 * 50
        private const val IO_BUFFER_SIZE = 8 * 1024
        private const val DISK_CACHE_INDEX = 0

        private val threadFactory = object : ThreadFactory {
            val mCount = AtomicInteger(1)

            override fun newThread(r: Runnable?): Thread {
                return Thread(r, "ImageLoader#${mCount.getAndIncrement()}")
            }
        }

        private val THREAD_POOL_EXECUTOR = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>(), threadFactory
        )

        fun build(context: Context): ImageLoader {
            return ImageLoader(context)
        }
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val result = msg.obj as LoaderResult
            val imageView = result.imageView
            val uri = imageView.getTag(TAG_KEY_URI)
            if (uri == result.uri) {
                imageView.setImageBitmap(result.bitmap)
            } else {
                Log.w(
                    TAG,
                    "ImageLoader.handleMessage(), set image bitmap, but url has changed, ignored!"
                )
            }
        }
    }

    private val context = context.applicationContext
    private val imageResizer = ImageResizer()
    private var memoryCache: LruCache<String, Bitmap>
    private var diskLruCache: DiskLruCache? = null
    private var isDiskLruCacheCreated = false

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = lruCache(maxSize = cacheSize, sizeOf = { _, bitmap ->
            bitmap.rowBytes * bitmap.height / 1024
        })
        val diskCacheDir = getDiskCacheDir(this.context)
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdir()
        }

        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                diskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE)
                isDiskLruCacheCreated = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun addBitmapToMemoryCache(key: String, bitmap: Bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap)
        }
    }

    private fun getBitmapFromMemCache(key: String): Bitmap? = memoryCache.get(key)

    /**
     * load bitmap from memory cache or disk cache or network async, then bind imageView and bitmap.
     * NOTE THAT: should run in UI Thread
     * @param uri http url
     * @param imageView bitmap's bind object
     */
    fun bindBitmap(uri: String, imageView: ImageView) {
        bindBitmap(uri, imageView, 0, 0)
    }

    fun bindBitmap(uri: String, imageView: ImageView, reqWidth: Int, reqHeight: Int) {
        imageView.setTag(TAG_KEY_URI, uri)
        val bitmap: Bitmap? = loadBitmapFromMemCache(uri)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
            return
        }

        THREAD_POOL_EXECUTOR.execute {
            val loadedBitmap: Bitmap? = loadBitmap(uri, reqWidth, reqHeight)
            if (loadedBitmap != null) {
                val loaderResult = LoaderResult(imageView, uri, loadedBitmap)
                handler.obtainMessage(MESSAGE_POST_RESULT, loaderResult).sendToTarget()
            }
        }
    }

    /**
     * load bitmap from memory cache or disk cache or network.
     * @param uri http url
     * @param reqWidth the width ImageView desired
     * @param reqHeight the height ImageView desired
     * @return bitmap, maybe null.
     */
    fun loadBitmap(uri: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        var bitmap = loadBitmapFromMemCache(uri)
        if (bitmap != null) {
            Log.d(TAG, "ImageLoader.loadBitmap(), loadBitmapFromMemCache, url: $uri")
            return bitmap
        }
        try {
            bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight)
            if (bitmap != null) {
                Log.d(TAG, "ImageLoader.loadBitmap(), loadBitmapFromDisk, url: $uri")
                return bitmap
            }
            bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight)
            Log.d(TAG, "ImageLoader.loadBitmap(), loadBitmapFromHttp, url: $uri")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (bitmap == null && !isDiskLruCacheCreated) {
            Log.w(TAG, "ImageLoader.loadBitmap(), encounter error, DiskLruCache is not created.")
            bitmap = downloadBitmapFromUrl(uri)
        }
        return bitmap
    }

    private fun loadBitmapFromMemCache(url: String): Bitmap? =
        getBitmapFromMemCache(hashKeyFormUrl(url))

    @Throws(IOException::class)
    private fun loadBitmapFromHttp(url: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw RuntimeException("can not visit network from UI Thread.")
        }

        val dlc = diskLruCache ?: return null

        val key = hashKeyFormUrl(url)
        val editor: DiskLruCache.Editor? = dlc.edit(key)
        if (editor != null) {
            val os = editor.newOutputStream(DISK_CACHE_INDEX)
            if (downloadUrlToStream(url, os)) {
                editor.commit()
            } else {
                editor.abort()
            }
            dlc.flush()
        }

        return loadBitmapFromDiskCache(url, reqWidth, reqHeight)
    }

    @Throws(IOException::class)
    private fun loadBitmapFromDiskCache(url: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap from UI Thread, it's not recommended!")
        }

        val dlc = diskLruCache ?: return null

        var bitmap: Bitmap? = null
        val key = hashKeyFormUrl(url)
        val snapShot = dlc.get(key)
        if (snapShot != null) {
            val fis = snapShot.getInputStream(DISK_CACHE_INDEX) as FileInputStream
            bitmap = imageResizer.decodeSampledBitmapFromFileDescriptor(fis.fd, reqWidth, reqHeight)
                ?.also {
                    addBitmapToMemoryCache(key, it)
                }
        }

        return bitmap
    }

    private fun downloadUrlToStream(urlString: String, outputStream: OutputStream): Boolean {
        var urlConnection: HttpURLConnection? = null
        var bos: BufferedOutputStream? = null
        var bis: BufferedInputStream? = null
        try {
            urlConnection = URL(urlString).openConnection() as HttpURLConnection
            bis = BufferedInputStream(urlConnection.inputStream, IO_BUFFER_SIZE)
            bos = BufferedOutputStream(outputStream, IO_BUFFER_SIZE)
            var b: Int = bis.read()
            while (b != -1) {
                bos.write(b)
                b = bis.read()
            }
            return true
        } catch (e: IOException) {
            Log.e(TAG, "downloadBitmap failed.$e")
        } finally {
            urlConnection?.disconnect()
            MyUtils.close(bos)
            MyUtils.close(bis)
        }
        return false
    }

    private fun downloadBitmapFromUrl(urlString: String): Bitmap? {
        var urlConnection: HttpURLConnection? = null

        return try {
            urlConnection = URL(urlString).openConnection() as HttpURLConnection
            BufferedInputStream(urlConnection.inputStream, IO_BUFFER_SIZE).use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error in downloadBitmap: $e")
            null
        } finally {
            urlConnection?.disconnect()
        }
    }

    private fun hashKeyFormUrl(url: String): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(url.toByteArray())
            bytesToHexString(digest.digest())
        } catch (e: NoSuchAlgorithmException) {
            url.hashCode().toString()
        }
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(0xff and bytes[i].toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        Log.d(TAG, "ImageLoader.bytesToHexString(), sb = $sb")
        return sb.toString()
    }

    private fun getDiskCacheDir(context: Context): File {
        val cachePath = context.externalCacheDir?.path ?: context.cacheDir.path
        return File(cachePath + File.pathSeparator + "bitmap")
    }

    private fun getUsableSpace(path: File): Long {
        return path.usableSpace
    }

    private data class LoaderResult(
        var imageView: ImageView,
        var uri: String,
        var bitmap: Bitmap
    )
}