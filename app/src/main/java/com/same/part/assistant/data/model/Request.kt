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


/**
 * 更新店铺信息
 */
data class RequestUpdateShopInfo(
    val brand: String,
    val img: String,
    val name: String
)

data class RequestUpdateShopInfo1(
    val page: String = "0",
    val size: String= "10",
    val name: String= "",
val type:String = "1,2"
)





