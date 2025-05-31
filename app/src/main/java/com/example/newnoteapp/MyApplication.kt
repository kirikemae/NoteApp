package com.example.newnoteapp

import android.app.Application
import com.example.newnoteapp.di.appModule
import com.example.newnoteapp.di.databaseModule  // ДОБАВЛЯЕМ импорт
import com.example.newnoteapp.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(
                databaseModule,
                networkModule,
                appModule
            )
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        com.example.newnoteapp.data.local.database.NoteDatabase.closeDatabase()
    }
}
