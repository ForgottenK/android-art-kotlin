package com.example.myapplication.chapter_12

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.FileDescriptor

/**
 *    @author wangruixiang
 *    @date 2021/4/27 12:21 AM
 */
class ImageResizer {
    companion object {
        const val TAG = "wangruixiang"
    }

    fun decodeSampledBitmapFromResource(res: Resources, resId: Int, reqWidth: Int, reqHeight: Int): Bitmap? {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    fun decodeSampledBitmapFromFileDescriptor(fd: FileDescriptor, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFileDescriptor(fd, null, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFileDescriptor(fd, null, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1
        }

        // Raw width and height of image
        val width = options.outWidth
        val height = options.outHeight
        Log.d(TAG, "ImageResizer.calculateInSampleSize(), width = $width, height = $height")
        // Calculate the largest inSampleSize value that is a power of 2
        // and keeps both width and height larger than the requested width and height.
        var inSampleSize = 1
        while (width / (inSampleSize * 2) >= reqWidth && height / (inSampleSize * 2) >= reqHeight) {
            inSampleSize *= 2
        }
        return inSampleSize
    }
}