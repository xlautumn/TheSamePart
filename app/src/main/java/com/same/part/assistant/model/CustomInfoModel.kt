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