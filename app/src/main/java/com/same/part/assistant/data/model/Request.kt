package com.same.part.assistant.data.model

import com.same.part.assistant.app.util.CacheUtil

/**
 * 请求登录
 */
data class RequestShopUserLogin(
    val UserName: String,
    val UserPwd: String,
    val rememberMe: Boolean
)

/**
 * 请求账号信息
 */
data class RequestShopUserInfo(
    val token: String
)

/**
 * 更新店铺信息
 */
data class RequestUpdateShopInfo(
    val content: String,
    val img: String,
    val name: String
)

/**
 * 添加/编辑商品分类
 */
data class RequestShopCategoryInfo(
    val img: String,
    val name: String,
    val sequence: String,
    val description: String,
    val parentId: String,
    val shopId: Int? = CacheUtil.getShopId(),
    val appKey: String = CacheUtil.getAppKey(),
    val appSecret: String = CacheUtil.getAppSecret()
)

/**
 * appKey  appSecret
 */
data class RequestAppInfo(
    val appKey: String = CacheUtil.getShopUserModel()?.AccessToken?.easyapi?.appKey ?: "",
    val appSecret: String = CacheUtil.getShopUserModel()?.AccessToken?.easyapi?.appSecret ?: ""
)

data class CategoryData(
    var productCategoryId: String? = "",
    var addTime: String? = "",
    var updateTime: String? = "",
    var name: String? = "",
    var img: String? = "",
    var description: String? = "",
    var unit: String? = "",
    var brands: ArrayList<String>? = null,
    var sequence: String? = "",
    var ifShow: Boolean? = false,
    var sons: ArrayList<CategoryData>? = null,
    var isSelected: Boolean = false
)

data class ProductData(
    var content: ProductDetailData? = null,
    var pageable: ProductDetailPageable? = null,
    var totalElements: Int? = 0,
    var totalPages: Int? = 0,
    var last: Boolean? = false,
    var number: Int? = 0,
    var size: Int? = 0,
    var sort: ProductDetailSort? = null,
    var numberOfElements: Int? = 0,
    var first: Boolean? = false,
    var empty: Boolean? = false
)

data class ProductDetailData(
    var productId: String = "",
    var name: String? = "",
    var price: String? = "",
    var img: String? = "",
    var hasSku:Boolean = false
)


data class ProductDetailSku(
    var productSkuId: String? = "",
    var weight: String? = ""
)

data class ProductDetailPageable(
    var sort: ProductDetailSort? = null,
    var offset: Int? = 0,
    var pageSize: Int? = 0,
    var pageNumber: Int? = 0,
    var unpaged: Boolean? = false,
    var paged: Boolean? = false
)

data class ProductDetailSort(
    var sorted: Boolean? = false,
    var unsorted: Boolean? = false,
    var empty: Boolean? = false
)

data class CreateOrUpdateGoodsInfo(
    val barcode: String,
    val img: String,
    val name: String,
    val price: String,
    val productCategoryId: String,
    val sequence: String,
    val type: String,
    val unit: String,
    val quantity: String,
    val imgs: String = img,
    val shopId: String = CacheUtil.getShopId()?.toString() ?: "",
    val appKey: String = CacheUtil.getAppKey(),
    val appSecret: String = CacheUtil.getAppSecret(),
    val state: Int = 1,
    val audit_state: Int = 2,
    var id: String = ""
)

/**
 * 创建优惠劵活动
 */
data class RequestCreateCouponInfo(
    val title: String,
    val totalQty: Int,
    val conditionPrice: Double,
    val denominations: Double,
    val ifLimit: Int,
    val validEndTime: String,
    val validStartTime: String,
    val shopId: Int? = CacheUtil.getShopId(),
    val appKey: String = CacheUtil.getAppKey(),
    val appSecret: String = CacheUtil.getAppSecret()
)

/**
 * 创建购物车
 */
data class RequestCreateCart(
    val appKey: String = CacheUtil.getAppKey(),
    val appSecret: String = CacheUtil.getAppSecret(),
    val accessToken: String = CacheUtil.getToken(),//登录token
    val category: String = "采购商品",//类型描述，默认填写采购商品
    val endTime: String = "",//存活截止时间
    val productId: String,//商品ID
    val productSkuNumber: String,//规格编号，统一规格传空，多规格数据格式：productId（商品id）- productSkuId（商品规格id）
    val properties: String,//备注属性（逗号）
    val quantity: Int//数量（所有同件商品的）
)

/**
 * 修改购物车
 */
data class RequestUpdateCart(
    val appKey: String = CacheUtil.getAppKey(),
    val appSecret: String = CacheUtil.getAppSecret(),
    val quantity: Int
)

/**
 * 创建商品订单
 */
data class RequestCreateOrder(
    val appKey: String = CacheUtil.getAppKey(),
    val appSecret: String = CacheUtil.getAppSecret(),
    val shopPayId: String = CacheUtil.getShopId()?.toString() ?: "",
    val addressId: String,
    val cartIds: String,
    val category: String,//在线付款，货到付款
    val deliveryType: String = "物流配送",
    val orderRemarks: List<OrderRemark>,
    val orderType: String = "1",//1 采购订单
    val userId: String
) {
    data class OrderRemark(
        val properties: String = "",
        val remark: String = "",
        val shopId: String
    )
}

/**
 * 下单到支付或者微信
 */
data class RequestPay(
    val appKey: String = CacheUtil.getAppKey(),
    val appSecret: String = CacheUtil.getAppSecret(),
    val payment: String,
    val userMoney: String? = null
) {
    companion object {
        const val PAYMENT_WECHAT = "微信"
        const val PAYMENT_ALIPAY = "支付宝"
    }
}

data class PropertyData(
    var project: String = "",
    var name: String = "",
    var isSelected: Boolean = false
)

data class ProductSku(
    val productSkuId: String = "",
    val number: String = "",
    val price: String = "",
    val properties: LinkedHashSet<String>,
    val weight :String = "0"
)

data class ChangePwdInfo(
    val id: String,
    val password: String
)


data class CreateMemberCard(
    val description: String,
    val discount: Double,
    val lifetime: String,
    val name: String,
    val receiveWay: Int,
    val shopId: Int? = CacheUtil.getShopId(),
    val appKey: String = CacheUtil.getAppKey(),
    val appSecret: String = CacheUtil.getAppSecret()
)

data class RequestConformDelivery(val appKey: String = CacheUtil.getAppKey(),
                           val appSecret: String = CacheUtil.getAppSecret())

data class RequestCreateProduct(
    val barcode: String,
    val img: String,
    val name: String,
    val price: String,
    val productCategoryId: String,
    val sequence: String,
    val type: String,
    val unit: String,
    val quantity: String,
    val state: Int = 1,
    val productSkus: List<ProductSkuV2>?,
    val shopId: String = CacheUtil.getShopId()?.toString() ?: "",
    val accessToken: String = CacheUtil.getToken(),
    val appKey: String = CacheUtil.getAppKey(),
    val appSecret: String = CacheUtil.getAppSecret()
)














