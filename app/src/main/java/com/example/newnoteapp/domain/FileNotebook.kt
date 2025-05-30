package com.example.newnoteapp.domain

import android.content.Context
import org.json.JSONArray
import java.io.File

class FileNotebook private constructor(
    private val notes: MutableList<Note>
) {

    companion object {
        private const val FILE_NAME = "notebook.json"

        fun load(context: Context): FileNotebook {
            val file = File(context.filesDir, FILE_NAME)
            val notes = mutableListOf<Note>()

            if (file.exists()) {
                val jsonArray = JSONArray(file.readText())
                for (i in 0 until jsonArray.length()) {
                    val jsonNote = jsonArray.getJSONObject(i)
                    Note.parse(jsonNote)?.let { notes.add(it) }
                }
            }

            val notebook = FileNotebook(notes)
            notebook.removeExpiredNotes() // удаление самоуничтожающихся заметок
            return notebook
        }
    }

    val allNotes: List<Note>
        get() = notes.toList()

    fun addNote(note: Note) {
        notes.add(note)
    }

    fun removeNote(uid: String): Boolean {
        return notes.removeIf { it.uid == uid }
    }

    fun removeExpiredNotes(): Int {
        val currentTime = System.currentTimeMillis()
        val initialSize = notes.size

        notes.removeIf { note ->
            note.selfDestructDate?.let { it <= currentTime } ?: false
        }

        return initialSize - notes.size
    }

    fun save(context: Context): Boolean {
        return try {
            val jsonArray = JSONArray()
            notes.forEach { note ->
                jsonArray.put(note.json)
            }
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(jsonArray.toString())
            true
        } catch (_: Exception) {
            false
        }
    }
}
