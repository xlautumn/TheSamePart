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