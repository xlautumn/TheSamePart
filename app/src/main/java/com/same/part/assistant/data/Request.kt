package com.same.part.assistant.data


data class RequestShopUserLogin(
    val UserName: String,
    val UserPwd: String,
    val rememberMe: Boolean
)