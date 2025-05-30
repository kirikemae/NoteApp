package com.example.newnoteapp.data.remote

import com.example.newnoteapp.domain.model.Note

interface NoteRemoteDataSource {
    suspend fun getAllNotes(): List<Note>
    suspend fun uploadNote(note: Note)
    suspend fun deleteNote(id: String)
}
