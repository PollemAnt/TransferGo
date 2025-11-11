package com.example.transfergo.data.repository

import com.example.transfergo.data.api.FxApi
import com.example.transfergo.util.FxResult
import java.io.IOException

class FxRepository(private val api: FxApi): Repository {
    override suspend fun convert(from: String, to: String, amount: Double): FxResult<Pair<Double, Double>> {
        return try {
            val response = api.getFxRate(from, to, amount)
            FxResult.Success(response.rate to response.toAmount)
        } catch (e: IOException) {
            FxResult.Error("Network error: " + e.message)
        } catch (e: Exception) {
            FxResult.Error("Conversion error: " + e.message)
        }
    }
}

interface Repository {
    suspend fun convert(from: String, to: String, amount: Double): FxResult<Pair<Double, Double>>
}

