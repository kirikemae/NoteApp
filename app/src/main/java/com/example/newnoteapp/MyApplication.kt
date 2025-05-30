package com.example.newnoteapp

import android.app.Application
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext
import com.example.newnoteapp.di.appModule

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
