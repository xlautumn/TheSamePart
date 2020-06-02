package com.same.part.assistant.data.model

/**
 * 购物车中的商品
 */
data class ShopProduct(val productDetailData: ProductDetailData,var num: Int = 0,val cartId :String)