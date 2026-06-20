package com.example.myapplication

import android.app.Application
import com.example.myapplication.Token.AndroidTokenManager
import com.example.myapplication.http_api.appModules
import com.example.myapplication.http_api.detailviewModelModule
import com.example.myapplication.http_api.networkModule
import com.example.myapplication.http_api.repositoryModule
import com.example.myapplication.http_api.viewModelModule
import com.example.myapplication.token.TokenManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            val androidModule = module {
                single<TokenManager> { AndroidTokenManager(get()) }
            }
            modules(appModules + androidModule)
        }
    }
}