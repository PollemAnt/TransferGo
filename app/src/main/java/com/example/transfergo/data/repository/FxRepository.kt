package com.example.transfergo.data.repository

import com.example.transfergo.data.api.FxApi
import com.example.transfergo.util.FxResult
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class FxRepository(private val api: FxApi): Repository {
    override suspend fun convert(from: String, to: String, amount: Double): FxResult<Pair<Double, Double>> {
        return try {
            val response = api.getFxRate(from, to, amount)
            FxResult.Success(response.rate to response.toAmount)
        } catch (e: IOException) {
            FxResult.Error("Network error: ${e.message ?: "Unknown network error"}")
        } catch (e: HttpException) {
            FxResult.Error("HTTP error ${e.code()}: ${e.message ?: "Unknown error"}")
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            FxResult.Error("Conversion error: ${e.message ?: "Unknown error"}")
        }
    }
}

interface Repository {
    suspend fun convert(from: String, to: String, amount: Double): FxResult<Pair<Double, Double>>
}

