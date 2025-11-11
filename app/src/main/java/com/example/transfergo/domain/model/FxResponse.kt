package com.example.transfergo.domain.model

data class FxResponse(
    val from: String,
    val to: String,
    val rate: Double,
    val fromAmount: Double,
    val toAmount: Double
)