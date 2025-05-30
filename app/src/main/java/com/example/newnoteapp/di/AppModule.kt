package com.example.newnoteapp.di

import com.example.newnoteapp.data.local.FileNotebook
import com.example.newnoteapp.data.local.LocalDataSource
import com.example.newnoteapp.data.remote.NoteRemoteDataSource
import com.example.newnoteapp.data.repository.NoteRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.newnoteapp.ui.viewmodel.NoteViewModel
import com.example.newnoteapp.data.remote.DummyNoteRemoteDataSource

val appModule = module {
    single { FileNotebook(androidContext()) }
    single<LocalDataSource> { FileNotebook(androidContext()) }
    single<NoteRemoteDataSource> { DummyNoteRemoteDataSource() }
    single { NoteRepositoryImpl(get(), get()) }
    viewModel { NoteViewModel(get()) }
}
