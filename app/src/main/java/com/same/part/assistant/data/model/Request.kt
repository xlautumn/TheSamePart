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
    val brand: String,
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
    val appKey: String = CacheUtil.getShopUserModel()?.AccessToken?.easyapi?.appKey?:"",
    val appSecret: String = CacheUtil.getShopUserModel()?.AccessToken?.easyapi?.appSecret?:""
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
    var productId: String? = "",
    var name: String? = "",
    var price: String? = "",
    var img: String? = "",
    var productSku: ArrayList<ProductDetailSku>? = null,
    var cartNum: Int = 0
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












