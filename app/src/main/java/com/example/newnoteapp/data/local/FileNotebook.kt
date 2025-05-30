package com.example.newnoteapp.data.local

import android.content.Context
import android.util.Log
import com.example.newnoteapp.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.io.File

class FileNotebook(
    private val context: Context
) : LocalDataSource {

    private val notesFlow = MutableStateFlow<List<Note>>(emptyList())
    private val file: File
        get() = File(context.filesDir, FILE_NAME)

    companion object {
        private const val FILE_NAME = "notebook.json"
        private const val TAG = "FileNotebook"
    }

    init {
        loadFromFile()
    }

    private fun loadFromFile() {
        Log.d(TAG, "Загрузка заметок из кэша (файл)")
        val notes = mutableListOf<Note>()

        if (file.exists()) {
            try {
                val jsonArray = JSONArray(file.readText())
                for (i in 0 until jsonArray.length()) {
                    val jsonNote = jsonArray.getJSONObject(i)
                    Note.parse(jsonNote)?.let { notes.add(it) }
                }
                Log.d(TAG, "Загружено ${notes.size} заметок из кэша")
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при чтении файла кэша", e)
            }
        } else {
            Log.d(TAG, "Файл кэша не существует, создаем пустой список")
        }

        val beforeCleanup = notes.size
        notes.removeIf {
            it.selfDestructDate?.let { date -> date <= System.currentTimeMillis() } == true
        }
        val afterCleanup = notes.size

        if (beforeCleanup != afterCleanup) {
            Log.d(TAG, "Удалено ${beforeCleanup - afterCleanup} заметок с истекшим сроком")
        }

        notesFlow.value = notes
    }

    private fun saveToFile() {
        Log.d(TAG, "Сохранение ${notesFlow.value.size} заметок в кэш")
        try {
            val jsonArray = JSONArray()
            notesFlow.value.forEach { jsonArray.put(it.json) }
            file.writeText(jsonArray.toString())
            Log.d(TAG, "Заметки успешно сохранены в кэш")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сохранении заметок в кэш", e)
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        Log.d(TAG, "Запрос всех заметок из кэша")
        return notesFlow.asStateFlow()
    }

    override fun getNoteById(id: String): Flow<Note?> {
        Log.d(TAG, "Запрос заметки с ID: $id из кэша")
        return notesFlow.asStateFlow().map { notes ->
            notes.find { it.uid == id }.also { note ->
                if (note != null) {
                    Log.d(TAG, "Заметка с ID: $id найдена в кэше")
                } else {
                    Log.d(TAG, "Заметка с ID: $id не найдена в кэше")
                }
            }
        }
    }

    override suspend fun saveNote(note: Note) {
        Log.d(TAG, "Сохранение заметки в кэш: ${note.title} (ID: ${note.uid})")
        notesFlow.update { currentNotes ->
            val existingIndex = currentNotes.indexOfFirst { it.uid == note.uid }
            if (existingIndex >= 0) {
                Log.d(TAG, "Обновление существующей заметки в кэше")
                currentNotes.toMutableList().apply { set(existingIndex, note) }
            } else {
                Log.d(TAG, "Добавление новой заметки в кэш")
                currentNotes + note
            }
        }
        saveToFile()
    }

    override suspend fun deleteNote(id: String) {
        Log.d(TAG, "Удаление заметки из кэша: ID $id")
        val sizeBefore = notesFlow.value.size
        notesFlow.update { currentNotes ->
            currentNotes.filterNot { it.uid == id }
        }
        val sizeAfter = notesFlow.value.size

        if (sizeBefore != sizeAfter) {
            Log.d(TAG, "Заметка с ID $id успешно удалена из кэша")
            saveToFile()
        } else {
            Log.w(TAG, "Заметка с ID $id не найдена в кэше для удаления")
        }
    }
}
