package com.example.transfergo.util

val SUPPORTED_CURRENCIES = listOf(
    "PLN", "EUR", "GBP", "UAH"
)

val SEND_LIMITS = mapOf(
    "PLN" to 20000.0,
    "EUR" to 5000.0,
    "GBP" to 1000.0,
    "UAH" to 50000.0
)