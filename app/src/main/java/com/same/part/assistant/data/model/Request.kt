package com.same.part.assistant.data.model

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



