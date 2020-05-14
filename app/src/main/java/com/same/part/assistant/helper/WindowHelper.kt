package com.same.part.assistant.helper

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 * 实现透明状态栏沉浸式
 */
fun setSystemUITransparent(
    context: Context,
    window: Window,
    //是否扩展到状态栏
    status: Boolean,
    //扩展到标题栏的View是否是浅色
    lightExpandView: Boolean,
    //扩展到状态栏的View
    expandView: View,
    //是否隐藏底部导航栏
    navigation: Boolean
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        var visibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (status) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = Color.TRANSPARENT
                if (lightExpandView) {
                    visibility = visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    visibility = visibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            }
        }
    }

}