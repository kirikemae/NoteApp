package com.example.newnoteapp.di

import com.example.newnoteapp.data.local.LocalDataSource
import com.example.newnoteapp.data.local.RoomLocalDataSource
import com.example.newnoteapp.data.local.database.NoteDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        NoteDatabase.getDatabase(androidContext())
    }

    single {
        get<NoteDatabase>().noteDao()
    }

    single<LocalDataSource> {
        RoomLocalDataSource(get())
    }
}
