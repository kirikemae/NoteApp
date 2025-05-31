package com.example.newnoteapp.di

import com.example.newnoteapp.data.repository.NoteRepositoryImpl
import com.example.newnoteapp.domain.repository.NoteRepository
import com.example.newnoteapp.ui.viewmodel.NoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<NoteRepository> {
        NoteRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get()
        )
    }

    viewModel {
        NoteViewModel(get<NoteRepository>())
    }
}
