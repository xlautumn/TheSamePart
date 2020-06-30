import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Process
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {
    private var mDefaultCrashHandler: Thread.UncaughtExceptionHandler? = null
    private var mContext: Context? = null
    fun init(context: Context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        mContext = context.applicationContext
    }

    /**
     * 当程序中有未被捕获的异常，系统将会调用这个方法
     *
     * @param t 出现未捕获异常的线程
     * @param e 得到异常信息
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            //保存到本地
            exportExceptionToSDCard(e)
            //下面也可以写上传的服务器的代码
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        e.printStackTrace()
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler!!.uncaughtException(t, e)
        } else {
            Process.killProcess(Process.myPid())
        }
    }

    /**
     * 导出异常信息到SD卡
     *
     * @param e
     */
    private fun exportExceptionToSDCard(e: Throwable) {
        //判断SD卡是否存在
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return
        }
        val time: String =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//        val file = File(getLogFilePath())
//        try {
//            //往文件中写入数据
//            val pw = PrintWriter(BufferedWriter(FileWriter(file,true)),true)
//
//            pw.println(time)
//            pw.println(appendPhoneInfo())
//            e.printStackTrace(pw)
//            pw.close()
//        } catch (e1: Exception) {
//            e1.printStackTrace()
//        }
    }

    /**
     * 获取手机信息
     */
    @Throws(PackageManager.NameNotFoundException::class)
    private fun appendPhoneInfo(): String {
        val sb = StringBuilder()
        //App版本
        sb.append("App Version: ")
        mContext?.let {
            val pm = it.packageManager
            val pi = pm.getPackageInfo(it.packageName, PackageManager.GET_ACTIVITIES)
            sb.append(pi.versionName)
            sb.append("_")
            sb.append(
                """
                ${pi.versionCode}
                
                """.trimIndent()
            )
        }

        //Android版本号
        sb.append("OS Version: ")
        sb.append(Build.VERSION.RELEASE)
        sb.append("_")
        sb.append(
            """
                ${Build.VERSION.SDK_INT}
                
                """.trimIndent()
        )

        //手机制造商
        sb.append("Vendor: ")
        sb.append(
            """
                ${Build.MANUFACTURER}
                
                """.trimIndent()
        )

        //手机型号
        sb.append("Model: ")
        sb.append(
            """
                ${Build.MODEL}
                
                """.trimIndent()
        )

        //CPU架构
        sb.append("CPU: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(Arrays.toString(Build.SUPPORTED_ABIS))
        } else {
            sb.append(Build.CPU_ABI)
        }
        return sb.toString()
    }

    companion object {
        val instance = CrashHandler()
    }
}