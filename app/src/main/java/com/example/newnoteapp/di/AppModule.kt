package com.example.newnoteapp.di

import com.example.newnoteapp.data.local.FileNotebook
import com.example.newnoteapp.data.local.LocalDataSource
import com.example.newnoteapp.data.remote.RemoteDataSource
import com.example.newnoteapp.data.repository.NoteRepositoryImpl
import com.example.newnoteapp.domain.repository.NoteRepository
import com.example.newnoteapp.ui.viewmodel.NoteViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Local Data Source
    single<LocalDataSource> {
        FileNotebook(androidContext())
    }

    // Repository
    single<NoteRepository> {
        NoteRepositoryImpl(
            localDataSource = get<LocalDataSource>(),
            remoteDataSource = get<RemoteDataSource>()
        )
    }

    // ViewModel
    viewModel {
        NoteViewModel(get<NoteRepository>())
    }
}
