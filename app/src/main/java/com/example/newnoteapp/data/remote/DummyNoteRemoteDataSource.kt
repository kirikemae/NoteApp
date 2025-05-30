package com.example.newnoteapp.data.remote

import android.util.Log
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.domain.model.Importance
import kotlinx.coroutines.delay

class DummyNoteRemoteDataSource : NoteRemoteDataSource {

    private val backendStorage = mutableMapOf<String, Note>()

    init {
        val testNote1 = Note(
            uid = "test-1",
            title = "Заметка с бэкенда 1",
            content = "Это тестовая заметка, загруженная с бэкенда",
            importance = Importance.HIGH
        )
        val testNote2 = Note(
            uid = "test-2",
            title = "Заметка с бэкенда 2",
            content = "Еще одна тестовая заметка",
            importance = Importance.NORMAL
        )
        backendStorage[testNote1.uid] = testNote1
        backendStorage[testNote2.uid] = testNote2
    }

    override suspend fun getAllNotes(): List<Note> {
        delay(1000)
        Log.d("DummyRemote", "Loading ${backendStorage.size} notes from remote server")
        return backendStorage.values.toList()
    }

    override suspend fun uploadNote(note: Note) {
        delay(500)
        backendStorage[note.uid] = note
        Log.d("DummyRemote", "Uploading note to remote server: ${note.title}")
    }

    override suspend fun deleteNote(id: String) {
        delay(500)
        backendStorage.remove(id)
        Log.d("DummyRemote", "Deleting note with id $id from remote server")
    }
}
