package com.example.transfergo

import android.app.Application
import com.example.transfergo.koin.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TransferGoApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TransferGoApp)
            modules(appModule)
        }
    }
}