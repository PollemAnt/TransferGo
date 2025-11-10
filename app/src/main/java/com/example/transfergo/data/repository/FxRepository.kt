package com.example.transfergo.data.repository

import com.example.transfergo.data.api.FxApi
import com.example.transfergo.util.FxResult

class FxRepository(private val api: FxApi) {
    suspend fun convert(from: String, to: String, amount: Double): FxResult<Pair<Double, Double>> {
        return try {
            val response = api.getFxRate(from, to, amount)
            FxResult.Success(response.rate to response.convertedAmount)
        } catch (e: Exception) {
            FxResult.Error(e)
        }
    }
}

data class FxResponse(
    val rate: Double,
    val convertedAmount: Double
)