package com.same.part.assistant.data.model

data class WXPayRequest(
    val appId: String,
    val nonceStr: String,
    val packageValue: String,
    val partnerId: String,
    val prepayId: String,
    val sign: String,
    val timeStamp: String
)