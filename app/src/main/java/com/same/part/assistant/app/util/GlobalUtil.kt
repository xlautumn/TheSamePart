package com.same.part.assistant.app.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object GlobalUtil {

    /**
     * 获取当前时间的字符串，格式为yyyyMMddHHmmss。
     * @return 当前时间的字符串。
     */
    val currentDateString: String
        get() {
            val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            return sdf.format(Date())
        }

    /**
     * 获取传入通用资源标识符的后缀名。
     *
     * @param uri
     * 通用资源标识符
     * @return 通用资源标识符的后缀名。
     */
    fun getUriSuffix(uri: String): String {
        if (!TextUtils.isEmpty(uri)) {
            val doubleSlashIndex = uri.indexOf("//")
            val slashIndex = uri.lastIndexOf("/")
            if (doubleSlashIndex != -1 && slashIndex != -1) {
                if (doubleSlashIndex + 1 == slashIndex) {
                    return ""
                }
            }
            val dotIndex = uri.lastIndexOf(".")
            if (dotIndex != -1 && dotIndex > slashIndex) {
                return uri.substring(dotIndex + 1)
            }
        }
        return ""
    }

    /**
     * 生成资源对应的key。
     *
     * @param uri
     * 通用资源标识符
     * @param dir
     * 生成带有指定目录结构的key
     * @return 资源对应的key。
     */
    fun generateKey(uri: String, dir: String): String {
        val suffix = getUriSuffix(uri)
        val uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase()
        return if (TextUtils.isEmpty(dir)) {
            var key = "$currentDateString-$uuid"
            if (!TextUtils.isEmpty(suffix)) {
                key = "$key.$suffix"
            }
            key
        } else {
            var key = "$dir/$currentDateString-$uuid"
            if (!TextUtils.isEmpty(suffix)) {
                key = "$key.$suffix"
            }
            key.replace("//", "/")
        }
    }

    /**
     * 根据Uri获取文件真实地址
     */
    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var realPath: String? = null
        if (scheme == null) realPath =
            uri.path else if (ContentResolver.SCHEME_FILE == scheme) {
            realPath = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(
                uri, arrayOf(MediaStore.Images.ImageColumns.DATA),
                null, null, null
            )
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        realPath = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        if (TextUtils.isEmpty(realPath)) {
            if (uri != null) {
                val uriString = uri.toString()
                val index = uriString.lastIndexOf("/")
                val imageName = uriString.substring(index)
                var storageDir: File?
                storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )
                val file = File(storageDir, imageName)
                if (file.exists()) {
                    realPath = file.absolutePath
                } else {
                    storageDir =
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val file1 = File(storageDir, imageName)
                    realPath = file1.absolutePath
                }
            }
        }
        return realPath
    }

}