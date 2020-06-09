package com.same.part.assistant.utils

import android.content.Context
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

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

        /**
         * 获取当前年份
         */
        fun getCurrentYear(): String = "${Calendar.getInstance().get(Calendar.YEAR)}"

        /**
         * 获取当前月份
         */
        fun getCurrentMonth(): String = "${Calendar.getInstance().get(Calendar.MONTH) + 1}"

        /**
         * 获取当前日期
         */
        fun getCurrentDay(): String = "${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}"

        /**
         * 获取当前是第几周
         */
        fun getCurrentWeek(): String {
            val calendar = Calendar.getInstance()
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.timeInMillis = System.currentTimeMillis()
            return "${calendar.get(Calendar.WEEK_OF_YEAR)}"
        }

    }
}