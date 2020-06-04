package com.same.part.assistant.helper

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.alibaba.fastjson.JSONObject
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder
import com.allenliu.versionchecklib.v2.builder.UIData
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener
import com.scwang.smartrefresh.layout.SmartRefreshLayout


/**
 * 刷新结束的通知
 */
fun SmartRefreshLayout?.refreshComplete(hasMoreData: Boolean = true) {
    if (hasMoreData) {
        this?.finishRefresh()
        this?.finishLoadMore()
    } else {
        this?.finishRefreshWithNoMoreData()
        this?.finishLoadMoreWithNoMoreData()
    }
}


/**
 * 获取版本号
 *
 * @param context 上下文
 *
 * @return 版本号
 */
fun getVersionCode(context: Context): Int { //获取包管理器
    val pm: PackageManager = context.packageManager
    //获取包信息
    try {
        val packageInfo: PackageInfo = pm.getPackageInfo(context.packageName, 0)
        //返回版本号
        return packageInfo.versionCode
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return 0
}

/**
 * [获取应用程序版本名称信息]
 * @param context
 * @return 当前应用的版本名称
 */
fun getVersionName(context: Context): String? {
    try {
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo(
            context.packageName, 0
        )
        return packageInfo.versionName
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return null
}


fun detectVersion(context: Context) {
    val url =
        "https://api2.easyapi.com/app-upgrade/check.json?appKey=bVo0Mqax8c1wvJ4v&appSecret=rb6wgtx35bhanrfz&appPackage=com.same.part.assistant"
    AllenVersionChecker.getInstance().requestVersion().setRequestUrl(url)
        .request(object : RequestVersionListener {
            override fun onRequestVersionSuccess(
                downloadBuilder: DownloadBuilder?,
                result: String?
            ): UIData? {
                try {
                    val resultJson = JSONObject.parseObject(result)
                    resultJson?.getJSONObject("appUpgrade")?.apply {
                        val versionCode = getString("versionCode")
                        val versionName = getString("versionName")
                        val downloadUrl = getString("url")
                        if (versionCode.toInt() > getVersionCode(context)) {
                            return UIData
                                .create()
                                .setDownloadUrl(downloadUrl)
                                .setTitle("升级提醒")
                                .setContent("检测到新版本：$versionName\n是否下载?")
                        }
                    }

                } catch (e: Exception) {
                    return null
                }
                return null
            }

            override fun onRequestVersionFailure(message: String?) {

            }
        }).executeMission(context)

}

/**
 * 版本号比较
 *
 * @param version1
 * @param version2
 * @return
 */
fun compareVersion(version1: String, version2: String): Int {
    if (version1 == version2) {
        return 0
    }
    val version1Array = version1.split("\\.").toTypedArray()
    val version2Array = version2.split("\\.").toTypedArray()
    var index = 0
    // 获取最小长度值
    val minLen = version1Array.size.coerceAtMost(version2Array.size)
    var diff = 0
    // 循环判断每位的大小
    while (index < minLen
        && version1Array[index].toInt() - version2Array[index].toInt().also {
            diff = it
        } == 0
    ) {
        index++
    }
    return if (diff == 0) { // 如果位数不一致，比较多余位数
        for (i in index until version1Array.size) {
            if (version1Array[i].toInt() > 0) {
                return 1
            }
        }
        for (i in index until version2Array.size) {
            if (version2Array[i].toInt() > 0) {
                return -1
            }
        }
        0
    } else {
        if (diff > 0) 1 else -1
    }
}