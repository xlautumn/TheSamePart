package com.same.part.assistant.data.model

/**
 * 客户管理数据类
 */
data class CustomInfoModel(
    var avatarUrl: String,
    var nickname: String,
    var userId: String
)

/**
 * 会员管理数据类
 */
data class VipInfoModel(
    var avatarUrl: String,
    var nickname: String,
    var userId: String,
    var vipCard: String,
    var vipBalance: String
)

/**
 * 收银商品数据类
 */
data class CashierModel(
    var id: String,
    var name: String,
    var price: String,
    var unit: String,
    var status: Boolean
)

/**
 * 商品分类数据类
 */
data class ProductClassificationModel(
    var id: String,
    var name: String,
    var level: String
)

/**
 * 商品分类数据类
 */
data class ProductModel(
    var id: String,
    var name: String,
    var price: String,
    var repertory: String
)

/**
 * 商品分类数据类
 */
data class CashierOrderModel(
    var orderId: String,
    var amount: String,
    var payMethod: String,
    var time: String
)

/**
 * 商品分类数据类
 */
data class GoodItemModel(
    var avatar: String,
    var name: String,
    var number: String,
    var price: String
)

/**
 * 收银订单条目数据类
 */
data class CashierGoodItemModel(
    var avatar: String,
    var name: String,
    var number: String,
    var oldPrice: String,
    var newPrice: String
)

/**
 * 收银订单条目数据类
 */
data class PurchaseGoodItemModel(
    var avatar: String,
    var name: String,
    var number: String,
    var oldPrice: String,
    var newPrice: String
)

/**
 * 采购订单数据类
 */
data class PurchaseOrderModel(
    var id: String,
    var price: String,
    var time: String,
    var status: String
)

/**
 * 会员管理数据类
 */
data class VipCardInfoModel(
    var name: String,
    var denomination: String,
    var discount: String,
    var userCount: String,
    var operation: Boolean
)

/**
 * 优惠券数据类
 */
data class CouponInfoModel(
    var name: String,
    var remain: String,
    var total: String,
    var used: String,
    var status: String
)

/**
 * 账号数据类
 */
data class AccountModel(
    var name: String,
    var mobile: String,
    var role: String
)

/**
 * 支付方式数据类
 */
data class PayWayModel(
    var name: String,
    var isChecked: Boolean
)

/**
 * 登录信息
 */
data class ShopUserLoginModel(var accessToken: String, var expiresIn: Long)

/**
 * 账号信息
 */
data class ShopUserModel(
    val AccessToken: AccessToken,
    val UserShopDTO: List<UserShopDTO>
)

data class AccessToken(
    val accessToken: String,
    val addTime: String,
    val easyapi: Easyapi,
    val expiresIn: Int,
    val id: Int,
    val updateTime: String,
    val user: User
)

data class UserShopDTO(
    val addTime: String,
    val authorities: String,
    val ifCreater: Boolean,
    val mobile: String,
    val name: String,
    val remark: Any,
    val shop: Shop,
    val updateTime: String,
    val user: UserX,
    val userShopId: Int
)

data class Easyapi(
    val addTime: String,
    val appKey: String,
    val appSecret: String,
    val id: Int,
    val state: Int,
    val updateTime: String
)

data class User(
    val addTime: String,
    val age: Any,
    val birthAttrib: Any,
    val birthday: Any,
    val bloodType: Any,
    val card: Any,
    val city: Any,
    val constellation: Any,
    val district: Any,
    val easyapi: EasyapiX,
    val email: Any,
    val enabled: Boolean,
    val hobby: Any,
    val id: Int,
    val inviter: Any,
    val male: Any,
    val marital: Any,
    val mobile: String,
    val nickname: String,
    val openId: Any,
    val password: String,
    val photo: String,
    val points: Any,
    val province: Any,
    val realname: String,
    val roles: List<Any>,
    val tags: Any,
    val type: String,
    val updateTime: String,
    val userExtends: List<Any>,
    val username: String,
    val version: Int
)

data class EasyapiX(
    val addTime: String,
    val appKey: String,
    val appSecret: String,
    val id: Int,
    val state: Int,
    val updateTime: String
)

data class Shop(
    val addTime: String,
    val address: String,
    val auditResult: Any,
    val brand: Any,
    val businessLicense: String,
    val businessLicenseImg: String,
    val categoryIds: List<Int>,
    val city: String,
    val commentCount: Int,
    val content: Any,
    val corporate: Any,
    val count: Int,
    val distance: Double,
    val district: String,
    val email: String,
    val favoriteCount: Int,
    val firstScore: Double,
    val idCard: String,
    val idCardImg: String,
    val img: String,
    val integrity: Int,
    val lat: Any,
    val linkman: String,
    val lng: Any,
    val mobile: String,
    val monthOrders: Int,
    val moods: Int,
    val name: String,
    val `open`: Boolean,
    val phone: String,
    val platformRate: Int,
    val productCategoryCount: Int,
    val productCommentCount: Int,
    val productCount: Int,
    val province: String,
    val secondScore: Double,
    val shopId: Int,
    val state: Int,
    val thirdScore: Double,
    val totalOrders: Int,
    val updateTime: String
)

data class UserX(
    val addTime: String,
    val age: Any,
    val birthAttrib: Any,
    val birthday: Any,
    val bloodType: Any,
    val card: Any,
    val city: Any,
    val constellation: Any,
    val district: Any,
    val email: Any,
    val hobby: Any,
    val inviter: Any,
    val male: Any,
    val marital: Any,
    val mobile: String,
    val nickname: String,
    val openId: Any,
    val password: String,
    val photo: String,
    val points: Any,
    val province: Any,
    val realname: String,
    val tags: Any,
    val type: String,
    val updateTime: String,
    val userExtends: List<Any>,
    val userId: Int,
    val username: String,
    val version: Int
)

/**
 * 店铺信息
 */
data class ShopModel(
    val addTime: String,
    val address: String,
    val auditResult: Any,
    val brand: String,
    val businessLicense: String,
    val businessLicenseImg: String,
    val businessScope: Any,
    val businessScopeJava: Any,
    val cashName: Any,
    val cashPassword: Any,
    val categoryIds: List<Any>,
    val city: String,
    val commentCount: Int,
    val content: Any,
    val corporate: Any,
    val count: Int,
    val distance: Double,
    val district: String,
    val email: String,
    val favoriteCount: Int,
    val firstScore: Double,
    val idCard: String,
    val idCardImg: String,
    val ifFavorite: Boolean,
    val img: String,
    val integrity: Int,
    val lat: Any,
    val linkman: String,
    val lng: Any,
    val mobile: String,
    val monthOrders: Int,
    val moods: Int,
    val name: String,
    val `open`: Boolean,
    val phone: String,
    val platformRate: Int,
    val productCategoryCount: Int,
    val productCommentCount: Int,
    val productCount: Int,
    val province: String,
    val secondScore: Double,
    val sequence: Int,
    val shopExtends: List<Any>,
    val shopId: Int,
    val shopkeeperName: Any,
    val shopkeeperPassword: Any,
    val state: Int,
    val thirdScore: Double,
    val totalOrders: Int,
    val type: Any,
    val updateTime: String
)









