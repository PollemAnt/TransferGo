package com.example.transfergo.data.api

import com.example.transfergo.data.repository.FxResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FxApi {
    @GET("api/fx-rates")
    suspend fun getFxRate(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): FxResponse
}