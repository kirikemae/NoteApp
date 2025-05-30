package com.example.newnoteapp.domain.repository

import com.example.newnoteapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(id: String): Flow<Note?>
    suspend fun deleteNote(id: String)
    suspend fun addOrUpdate(note: Note)
}