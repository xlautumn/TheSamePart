package com.same.part.assistant.data.model

import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken

/**
 * 待加入购物车的商品
 */
data class ShopProduct(
    val productDetailData: ProductDetailData,
    var num: Int = 1,
    val productSkuNumber: String = "",
    val properties: String = ""
) {
    fun getProductKey(): String {
        return productDetailData.productId + productSkuNumber
    }
}

/**
 * 购物车中的商品
 */
data class CartProduct(
    val shopProduct: ShopProduct,
    val cartId: String,
    var price: String,
    val skuProperties: String = ""
) {
    /**
     *获取规格属性
     */
    fun getProperties(): List<String> {
        return if (skuProperties.isNullOrEmpty()) {
            listOf()
        } else {
            GsonUtils.fromJson<List<Property>>(skuProperties,
                object : TypeToken<List<Property>>() {}.type
            ).map { it.value }
        }
    }

    data class Property(
        val project: String = "",
        val value: String = ""
    )
}