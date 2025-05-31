package com.example.newnoteapp.data.remote

import com.example.newnoteapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getAllNotes(): Flow<List<Note>>
    suspend fun saveNote(note: Note): Note
    suspend fun deleteNote(id: String)
    suspend fun getNoteById(id: String): Note?
}
