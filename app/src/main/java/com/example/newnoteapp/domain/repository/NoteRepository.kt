package com.example.newnoteapp.domain.repository

import com.example.newnoteapp.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(id: String): Flow<Note?>
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun syncWithBackend()

    val syncStatus: StateFlow<String?>
    val isLoading: StateFlow<Boolean>
}
