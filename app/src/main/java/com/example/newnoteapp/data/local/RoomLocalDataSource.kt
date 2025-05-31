package com.example.newnoteapp.data.local

import android.util.Log
import com.example.newnoteapp.data.local.dao.NoteDao
import com.example.newnoteapp.data.local.mapper.NoteEntityMapper
import com.example.newnoteapp.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomLocalDataSource(
    private val noteDao: NoteDao
) : LocalDataSource {

    companion object {
        private const val TAG = "RoomLocalDataSource"
    }

    override fun getAllNotes(): Flow<List<Note>> {
        Log.d(TAG, "Получение всех заметок из Room БД (Flow)")
        return noteDao.getAllNotesFlow().map { entities ->
            NoteEntityMapper.entitiesToNotes(entities).also {
                Log.d(TAG, "Получено ${it.size} заметок из БД")
            }
        }
    }

    override suspend fun getNoteById(id: String): Note? = withContext(Dispatchers.IO) {
        Log.d(TAG, "Получение заметки по ID: $id")
        return@withContext try {
            noteDao.getNoteById(id)?.let { entity ->
                NoteEntityMapper.entityToNote(entity).also {
                    Log.d(TAG, "Заметка найдена: ${it.title}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении заметки $id", e)
            null
        }
    }

    override fun getNoteByIdFlow(id: String): Flow<Note?> {
        Log.d(TAG, "Получение заметки по ID (Flow): $id")
        return noteDao.getNoteByIdFlow(id).map { entity ->
            entity?.let {
                NoteEntityMapper.entityToNote(it).also {
                    Log.d(TAG, "Заметка найдена (Flow): ${it.title}")
                }
            }
        }
    }

    override suspend fun saveNote(note: Note): Unit = withContext(Dispatchers.IO) {
        Log.d(TAG, "Сохранение заметки: ${note.title}")
        try {
            val entity = NoteEntityMapper.noteToEntity(note)
            val result = noteDao.insertNote(entity)
            Log.d(TAG, "Заметка сохранена с ID: $result")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сохранении заметки: ${note.title}", e)
            throw e
        }
    }

    override suspend fun saveNotes(notes: List<Note>): Unit = withContext(Dispatchers.IO) {
        Log.d(TAG, "Сохранение ${notes.size} заметок")
        try {
            val entities = NoteEntityMapper.notesToEntities(notes)
            val results = noteDao.insertNotes(entities)
            Log.d(TAG, "Сохранено ${results.size} заметок")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сохранении списка заметок", e)
            throw e
        }
    }

    override suspend fun deleteNote(id: String): Unit = withContext(Dispatchers.IO) {
        Log.d(TAG, "Удаление заметки: $id")
        try {
            val result = noteDao.deleteNoteById(id)
            if (result > 0) {
                Log.d(TAG, "Заметка $id успешно удалена")
            } else {
                Log.w(TAG, "Заметка $id не найдена для удаления")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при удалении заметки: $id", e)
            throw e
        }
    }

    override suspend fun deleteAllNotes(): Unit = withContext(Dispatchers.IO) {
        Log.d(TAG, "Удаление всех заметок")
        try {
            val result = noteDao.deleteAllNotes()
            Log.d(TAG, "Удалено $result заметок")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при удалении всех заметок", e)
            throw e
        }
    }

    override suspend fun getUnsyncedNotes(): List<Note> {
        Log.d(TAG, "getUnsyncedNotes - будет реализовано в следующем этапе")
        return emptyList()
    }

    override suspend fun markAsSynced(id: String): Unit {
        Log.d(TAG, "markAsSynced - будет реализовано в следующем этапе")
    }

    override suspend fun getNotesCount(): Int = withContext(Dispatchers.IO) {
        return@withContext try {
            noteDao.getNotesCount().also {
                Log.d(TAG, "Количество заметок: $it")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении количества заметок", e)
            0
        }
    }
}
