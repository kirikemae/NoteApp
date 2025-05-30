package com.example.newnoteapp.data.local

import com.example.newnoteapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(id: String): Flow<Note?>
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(id: String)
}
