package com.example.newnoteapp.data.local

import com.example.newnoteapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: String): Note?
    fun getNoteByIdFlow(id: String): Flow<Note?>
    suspend fun saveNote(note: Note)
    suspend fun saveNotes(notes: List<Note>)
    suspend fun deleteNote(id: String)
    suspend fun deleteAllNotes()
    suspend fun getUnsyncedNotes(): List<Note>
    suspend fun markAsSynced(id: String)
    suspend fun getNotesCount(): Int
}
