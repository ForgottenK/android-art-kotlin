package com.example.myapplication.chapter_13

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Process
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 *    @author wangruixiang
 *    @date 2021/4/27 4:03 PM
 */

class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    companion object {
        private const val TAG = "wangruixiang"
        private const val DEBUG = true
        private const val FILE_NAME = "crash"
        private const val FILE_NAME_SUFFIX = ".trace"

        val instance = CrashHandler()
    }

    private var defaultCrashHandler: Thread.UncaughtExceptionHandler? = null
    private var context: Context? = null

    fun init(ctx: Context) {
        defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        context = ctx.applicationContext
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            //导出异常信息到SD卡中
            dumpExceptionToSDCard(e)
            uploadExceptionToServer()
            //这里可以通过网络上传异常信息到服务器，便于开发人员分析日志从而解决bug
        } catch (e: IOException) {
            e.printStackTrace()
        }
        e.printStackTrace()

        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        defaultCrashHandler?.uncaughtException(t, e) ?: Process.killProcess(Process.myPid())
    }

    @Throws(IOException::class)
    private fun dumpExceptionToSDCard(ex: Throwable) {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            if (DEBUG) {
                Log.w(TAG, "sdcard unmounted,skip dump exception")
                return
            }
        }
        val dir = getCrashDirPath() ?: return
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val current = System.currentTimeMillis()
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(current))
        val file = File(dir.path + FILE_NAME + time + FILE_NAME_SUFFIX)
        try {
            PrintWriter(BufferedWriter(FileWriter(file))).use {
                it.println(time)
                dumpPhoneInfo(it)
                it.println()
                ex.printStackTrace(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "CrashHandler.dumpExceptionToSDCard(), dump crash info failed")
        }
    }

    private fun getCrashDirPath(): File? {
        val ctx = context ?: return null
        val filePath = ctx.getExternalFilesDir(null)?.path ?: ctx.filesDir.path
        return File(filePath + File.pathSeparator + "crashLog")
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun dumpPhoneInfo(pw: PrintWriter) {
        val ctx = context ?: return
        val pm = ctx.packageManager
        val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)

        //app信息
        pw.println("App Version: ${pi.versionName}_${pi.versionCode}")
        //android版本号
        pw.println("OS Version: ${Build.VERSION.RELEASE}_${Build.VERSION.SDK_INT}")
        //手机制造商
        pw.println("Vendor: ${Build.MANUFACTURER}")
        //手机型号
        pw.println("Model: ${Build.MODEL}")
        //cpu架构
        pw.println("CPU ABI: ${Build.CPU_ABI}")
    }

    private fun uploadExceptionToServer() {
        //TODO Upload Exception Message To Your Web Server
    }
}