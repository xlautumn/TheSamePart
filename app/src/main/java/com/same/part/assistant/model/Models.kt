package com.same.part.assistant.model

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
    var specification: String,
    var operation: Boolean
)

/**
 * 商品分类数据类
 */
data class ProductClassificationModel(
    var id: String,
    var name: String,
    var count: String
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
