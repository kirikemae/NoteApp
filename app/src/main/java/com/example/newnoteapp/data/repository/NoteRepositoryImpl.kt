package com.example.newnoteapp.data.repository

import com.example.newnoteapp.data.local.LocalDataSource
import com.example.newnoteapp.data.remote.NoteRemoteDataSource
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import android.util.Log

class NoteRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: NoteRemoteDataSource
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> = localDataSource.getAllNotes()

    override fun getNoteById(id: String): Flow<Note?> = localDataSource.getNoteById(id)

    override suspend fun addOrUpdate(note: Note) {
        localDataSource.saveNote(note)

        try {
            remoteDataSource.uploadNote(note)
            Log.d("Repository", "Note ${note.uid} successfully synced with backend")
        } catch (e: Exception) {
            Log.e("Repository", "Failed to sync note ${note.uid} with backend", e)
        }
    }

    override suspend fun deleteNote(id: String) {
        localDataSource.deleteNote(id)

        try {
            remoteDataSource.deleteNote(id)
            Log.d("Repository", "Note $id successfully deleted from backend")
        } catch (e: Exception) {
            Log.e("Repository", "Failed to delete note $id from backend", e)
        }
    }

    suspend fun syncWithBackend() {
        try {
            Log.d("Repository", "Starting sync with backend...")

            val remoteNotes = remoteDataSource.getAllNotes()

            val localNotes = localDataSource.getAllNotes().first()

            remoteNotes.forEach { remoteNote ->
                localDataSource.saveNote(remoteNote)
            }

            Log.d("Repository", "Sync completed. Synced ${remoteNotes.size} notes")
        } catch (e: Exception) {
            Log.e("Repository", "Sync failed", e)
        }
    }
}
