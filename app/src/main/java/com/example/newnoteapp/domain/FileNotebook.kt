package com.example.newnoteapp.domain

import android.content.Context
import org.json.JSONArray
import java.io.File
import timber.log.Timber

class FileNotebook private constructor(
    private val notes: MutableList<Note>
) {

    companion object {
        private const val FILE_NAME = "notebook.json"

        fun load(context: Context): FileNotebook {
            Timber.d("Начинаем загрузку записной книжки из файла")
            val file = File(context.filesDir, FILE_NAME)
            val notes = mutableListOf<Note>()

            if (file.exists()) {
                try {
                    val jsonArray = JSONArray(file.readText())
                    Timber.d("Файл найден, загружено заметок: ${jsonArray.length()}")
                    for (i in 0 until jsonArray.length()) {
                        val jsonNote = jsonArray.getJSONObject(i)
                        Note.parse(jsonNote)?.let {
                            notes.add(it)
                            Timber.d("Заметка загружена: ${it.title} (uid=${it.uid})")
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Ошибка при загрузке файла записной книжки")
                }
            } else {
                Timber.d("Файл записной книжки не найден, создаём новый")
            }

            val notebook = FileNotebook(notes)
            val removedCount = notebook.removeExpiredNotes() // удаление самоуничтожающихся заметок
            if (removedCount > 0) {
                Timber.d("Удалено $removedCount просроченных заметок при загрузке")
            }
            return notebook
        }
    }

    val allNotes: List<Note>
        get() = notes.toList()

    fun addNote(note: Note) {
        Timber.d("Добавление новой заметки: ${note.title} (uid=${note.uid})")
        notes.add(note)
    }

    fun removeNote(uid: String): Boolean {
        Timber.d("Попытка удалить заметку с uid=$uid")
        val removed = notes.removeIf { it.uid == uid }
        if (removed) {
            Timber.d("Заметка с uid=$uid успешно удалена")
        } else {
            Timber.w("Заметка с uid=$uid не найдена")
        }
        return removed
    }

    fun removeExpiredNotes(): Int {
        val currentTime = System.currentTimeMillis()
        val initialSize = notes.size

        notes.removeIf { note ->
            val expired = note.selfDestructDate?.let { it <= currentTime } ?: false
            if (expired) {
                Timber.d("Удаление просроченной заметки: ${note.title} (uid=${note.uid})")
            }
            expired
        }

        val removedCount = initialSize - notes.size
        if (removedCount > 0) {
            Timber.d("Всего удалено просроченных заметок: $removedCount")
        }
        return removedCount
    }

    fun save(context: Context): Boolean {
        Timber.d("Сохраняем записную книжку с количеством заметок: ${notes.size}")
        return try {
            val jsonArray = JSONArray()
            notes.forEach { note ->
                jsonArray.put(note.json)
            }
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(jsonArray.toString())
            Timber.d("Записная книжка успешно сохранена")
            true
        } catch (e: Exception) {
            Timber.e(e, "Ошибка при сохранении записной книжки")
            false
        }
    }
}
