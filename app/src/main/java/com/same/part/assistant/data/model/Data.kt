package com.same.part.assistant.data.model

/**
 * 待加入购物车的商品
 */
data class ShopProduct(
    val productDetailData: ProductDetailData,
    var num: Int = 1,
    val productSkuNumber: String="",
    val properties: String=""
) {
    fun getProductKey(): String {
        return productDetailData.productId + productSkuNumber
    }
}

/**
 * 购物车中的商品
 */
data class CartProduct(val shopProduct: ShopProduct, val cartId: String)