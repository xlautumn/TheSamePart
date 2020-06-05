package com.same.part.assistant.data.model

data class GetAddressesByUserIdMsg(
    val `data`: List<Address>,
    val size: Int
)

data class Address(
    val addTime: String,
    val addr: String,
    val addressId: Int,
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
