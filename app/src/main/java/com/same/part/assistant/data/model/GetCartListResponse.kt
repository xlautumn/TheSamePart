package com.same.part.assistant.data.model

/**
 * 获取购物车列表返回的数据
 */
class GetCartListResponse : ArrayList<GetCartListResponseItem>()


data class GetCartListResponseItem(
    val carts: List<Cart>,
    val deliveryFee: String,
    val promotionType: Any,
    val promotionTypeName: Any,
    val shop: Shop
)

data class Cart(
    val addTime: String,
    val cartId: String,
    val category: String,
    val costPrice: Double,
    val endTime: Any,
    val img: String,
    val linePrice: Double,
    val name: String,
    val price: String,
    val product: Product,
    val productSku: ProductSkuX,
    val properties: String,
    val quantity: String,
    val shop: Shop,
    val skuProperties: String,
    val state: Int,
    val type: String,
    val updateTime: String,
    val user: User
)


data class ProductSkuX(
    val addTime: String,
    val barcode: Any,
    val costPrice: String,
    val finalPrice: FinalPrice,
    val img: String,
    val linePrice: String,
    val number: String,
    val price: String,
    val productSkuId: String,
    val properties: String,
    val quantity: Int,
    val state: Int,
    val totalSales: Int,
    val updateTime: String,
    val volume: Any,
    val warnQuantity: Int,
    val weight: String,
    val withHoldQuantity: Int
){
    data class FinalPrice(
        val linePrice: Double,
        val price: Double,
        val type: String
    )
}





data class Product(
    val addTime: String,
    val auditResult: Any,
    val auditState: Int,
    val barcode: Any,
    val brand: Any,
    val coinRate: Double,
    val costPrice: Double,
    val count: Int,
    val description: Any,
    val favoriteCount: Int,
    val finalPrice: Any,
    val groupIds: List<Any>,
    val grouponState: Int,
    val ifPlatformCut: Boolean,
    val ifWarn: Boolean,
    val img: String,
    val imgs: String,
    val limitDiscountState: Int,
    val linePrice: Double,
    val monthSales: Int,
    val name: String,
    val number: String,
    val platformCutRate: Double,
    val pointRate: Double,
    val price: String,
    val productCategory: String,
    val productExtends: List<ProductExtend>,
    val productId: String,
    val productSku: List<ProductSkuX>,
    val quantity: Int,
    val ratio: Double,
    val sequence: Int,
    val shop: Shop,
    val shopBrand: String,
    val shopName: String,
    val specification: Any,
    val state: Int,
    val stockDeduct: Int,
    val totalSales: Int,
    val unit: String,
    val updateTime: String,
    val warnQuantity: Int,
    val weight: Double,
    val withHoldQuantity: Int
)

data class ProductExtend(
    val addTime: String,
    val fieldKey: String,
    val fieldName: String,
    val fieldValue: String,
    val productExtendId: Int,
    val updateTime: String
)

