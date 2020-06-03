package com.same.part.assistant.utils

import android.content.Context
import java.math.BigDecimal
import java.math.RoundingMode

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


        /**
         * 保留两位小数
         *
         * @param value
         * @return
         */
        fun format2(value: String?): String? {
            var bd = BigDecimal(value)
            bd = bd.setScale(2, RoundingMode.HALF_UP)
            return bd.toPlainString()
        }
    }
}