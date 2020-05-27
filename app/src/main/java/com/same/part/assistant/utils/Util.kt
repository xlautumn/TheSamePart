package com.same.part.assistant.utils

import android.content.Context

class Util {
    companion object {

        @JvmStatic
        fun dip2px(context: Context, dip: Float): Int {
            var density = context.resources.displayMetrics.density
            return (dip * density + 0.5f).toInt()
        }

        @JvmStatic
        fun px2dip(context: Context, px: Float): Int {
            var density = context.resources.displayMetrics.density
            return (px / density + 0.5f).toInt()
        }
    }
}