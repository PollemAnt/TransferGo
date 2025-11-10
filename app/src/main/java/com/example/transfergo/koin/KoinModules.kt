package com.example.transfergo.koin

import com.example.transfergo.data.api.FxApi
import com.example.transfergo.data.repository.FxRepository
import com.example.transfergo.ui.converter.ConverterViewModel
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module{

    single {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://my.transfergo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(get())
            .build()
            .create(FxApi::class.java)
    }

    viewModel { ConverterViewModel(get()) }

    single { FxRepository(get()) }
}