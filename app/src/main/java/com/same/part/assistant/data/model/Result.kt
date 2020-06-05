package com.same.part.assistant.data.model

import com.qiniu.android.http.ResponseInfo
import java.io.Serializable

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
    var name: String,
    var photo: String,
    var discount: String,
    var mobile: String
)

/**
 * 收银商品数据类
 */
data class CashierModel(
    var id: String = "",
    var name: String = "",
    var price: String = "",
    var unit: String = "个",
    var status: String = "1",
    var img: String = "",
    var imgs: String = "",
    var productCategoryId: String = "",
    var barcode: String = "",
    var sequence: String = "",
    //1—代表是收银商品 ,称重商品 2—代表是收银商品，非称重3—代表非收银商品，称重商品4—代表非收银商品，非称重商品
    var type: String = "2",
    //	审核状态（0待审核，1审核通过，-1审核驳回） 默认值2
    var audit_state: String = "2",
    var quantity: String = "2147483647"
) : Serializable


/**
 * 商品分类数据类
 */
data class ProductClassificationModel(
    var id: String,
    var name: String,
    var parentId: String
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
    var no: String,
    var price: String,
    var payment: String,
    var addTime: String,
    var shopCouponPrice: String,
    var platformCouponPrice: String,
    var orderItemList: ArrayList<CashierGoodItemModel> = ArrayList()
) : Serializable

/**
 * 采购订单数据类
 */
data class PurchaseOrderModel(
    var no: String,
    var time: String,
    var price: String,
    var state: String,
    var payState: String,
    var statements: String,
    var province: String,
    var city: String,
    var district: String,
    var address: String,
    var addrName: String,
    var addrMobile: String,
    var payment: String,
    var nickname: String,
    var nicktel: String,
    var nickDeliveryTime: String,
    var nickServiceTime: String,
    var orderItemList: ArrayList<CashierGoodItemModel> = ArrayList()
) : Serializable

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
    var img: String,
    var name: String,
    var quantity: String,
    var price: String,
    var oldPrice: String = ""
) : Serializable

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
 * 优惠券数据类
 */
data class CouponInfoModel(
    var name: String,
    var issued: String,
    var remain: String,
    var used: String,
    var status: String,
    var statements: String
)

/**
 * 账号数据类
 */
data class AccountModel(
    var id: String,
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
    val UserShopDTO: List<UserShopDTO>,
    var AddressMsg: AddressMsg? = null
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

/**
 * 七牛云返回数据
 */
data class QiniuMode(
    val img: String,
    val qiniuResponseInfo: ResponseInfo?
)

/**
 * 收银商品详情
 */
data class CashierDetailMode(
    val addTime: String,
    val auditResult: Any,
    val auditState: Int,
    val barcode: String,
    val brand: Any,
    val coinRate: Double,
    val content: Any,
    val costPrice: Double,
    val count: Int,
    val deliveryTemplate: Any,
    val description: Any,
    val favoriteCount: Int,
    val groupIds: List<Any>,
    val ifPlatformCut: Boolean,
    val ifWarn: Boolean,
    val img: String,
    val imgs: String,
    val linePrice: Double,
    val monthSales: Int,
    val name: String,
    val number: String,
    val platformCutRate: Double,
    val pointRate: Double,
    val postFee: Double,
    val postType: Any,
    val price: Double,
    val productCategory: Any,
    val productExtends: List<Any>,
    val customCategoryProductId: Int,
    val productId: Int,
    val quantity: Int,
    val ratio: Double,
    val sequence: Int,
    val shop: Shop,
    val specification: Any,
    val state: Int,
    val totalSales: Int,
    val type: String,
    val unit: String,
    val updateTime: String,
    val volume: Any,
    val warnQuantity: Int,
    val weight: Double,
    val withHoldQuantity: Int
)

/**
 * 修改密码
 */
data class ChangePwdModel(
    val accessToken: Any,
    val addTime: String,
    val age: Any,
    val birthAttrib: Any,
    val birthday: Any,
    val bloodType: Any,
    val city: Any,
    val constellation: Any,
    val district: Any,
    val email: Any,
    val hobby: Any,
    val inviter: Any,
    val male: Any,
    val marital: Any,
    val mobile: Any,
    val nickname: String,
    val openId: Any,
    val password: String,
    val photo: String,
    val points: Int,
    val province: Any,
    val realname: Any,
    val tags: Any,
    val type: String,
    val updateTime: String,
    val userExtends: List<Any>,
    val userId: Int,
    val username: String,
    val version: Int
)

/**
 * 会员管理数据类
 */
data class MemberCardState(
    val hasMore: Boolean,
    val isRefresh: Boolean,
    val memberCardList: ArrayList<MemberCardModel>
)

data class MemberCardModel(
    val cardId: Int,
    val name: String,
    val discount: Int,
    val userCount: Int,
    val description: String
) : Serializable

data class AddressMsg(
    val `data`: List<Address>,
    val size: Int
)

data class Address(
    val addTime: String,
    val addr: String,
    val addressId: String,
    val city: String,
    val district: String,
    val ifDefault: Boolean,
    val lat: Any,
    val lng: Any,
    val mobile: String,
    val name: String,
    val province: String,
    val updateTime: String,
    val user: User,
    val weixin: Any,
    val zip: Any
)



















