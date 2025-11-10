package com.example.transfergo.data.repository

import com.example.transfergo.data.api.FxApi
import com.example.transfergo.util.FxResult

class FxRepository(private val api: FxApi) {
    suspend fun convert(from: String, to: String, amount: Double): FxResult<Pair<Double, Double>> {
        return try {
            val response = api.getFxRate(from, to, amount)
            FxResult.Success(response.rate to response.toAmount)
        } catch (e: Exception) {
            FxResult.Error(e)
        }
    }
}

data class FxResponse(
    val from: String,
    val to: String,
    val rate: Double,
    val fromAmount: Double,
    val toAmount: Double
)