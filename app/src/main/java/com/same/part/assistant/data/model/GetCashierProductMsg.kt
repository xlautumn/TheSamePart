package com.same.part.assistant.data.model

data class GetCashierProductMsg(
    val `data`: List<CashierProduct>,
    val size: Int
)