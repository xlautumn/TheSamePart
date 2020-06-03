package com.same.part.assistant.utils

import java.math.BigDecimal

object CalculateUtil {
    fun add(price1:String,price2: String):String{
        val bigDecimal1 = BigDecimal(price1)
        val bigDecimal2 = BigDecimal(price2)
        return bigDecimal1.add(bigDecimal2).toPlainString()
    }
    fun minus(price1:String,price2: String):String{
        val bigDecimal1 = BigDecimal(price1)
        val bigDecimal2 = BigDecimal(price2)
        return bigDecimal1.minus(bigDecimal2).toPlainString()
    }
    fun multiply(price1:String,price2: String):String{
        val bigDecimal1 = BigDecimal(price1)
        val bigDecimal2 = BigDecimal(price2)
        return bigDecimal1.multiply(bigDecimal2).toPlainString()
    }
    fun divide(price1:String,price2: String):String{
        val bigDecimal1 = BigDecimal(price1)
        val bigDecimal2 = BigDecimal(price2)
        return bigDecimal1.divide(bigDecimal2).toPlainString()
    }

    fun signum(price:String)= BigDecimal(price).signum()
}


