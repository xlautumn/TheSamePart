package com.same.part.assistant.data.model

data class GetCashierCategoryDetail(
    val customCategoryId: String,
    val customCategoryProducts: List<CustomCategoryProduct>
)

data class CustomCategoryProduct(
    val product: CashierProduct
)
