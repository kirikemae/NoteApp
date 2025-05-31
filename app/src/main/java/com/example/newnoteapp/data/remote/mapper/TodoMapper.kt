package com.example.newnoteapp.data.remote.mapper

import com.example.newnoteapp.data.remote.dto.TodoItemDto
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.domain.model.Importance
import android.graphics.Color

object TodoMapper {

    fun todoItemDtoToNote(dto: TodoItemDto): Note {
        return Note(
            uid = dto.id,
            title = extractTitle(dto.text),
            content = extractContent(dto.text),
            importance = mapImportanceFromApi(dto.importance),
            selfDestructDate = dto.deadline,
            color = parseColor(dto.color)
        )
    }

    fun noteToTodoItemDto(note: Note): TodoItemDto {
        val currentTime = System.currentTimeMillis()

        return TodoItemDto(
            id = note.uid,
            text = formatText(note.title, note.content),
            importance = mapImportanceToApi(note.importance),
            deadline = note.selfDestructDate,
            done = false,
            color = formatColor(note.color),
            createdAt = currentTime,
            changedAt = currentTime,
            lastUpdatedBy = "android_app"
        )
    }

    private fun extractTitle(text: String): String {
        val lines = text.split("\n")
        return if (lines.isNotEmpty()) lines[0] else ""
    }

    private fun extractContent(text: String): String {
        val lines = text.split("\n")
        return if (lines.size > 1) {
            lines.drop(1).joinToString("\n")
        } else {
            ""
        }
    }

    private fun formatText(title: String, content: String): String {
        return if (content.isNotEmpty()) {
            "$title\n$content"
        } else {
            title
        }
    }

    private fun mapImportanceFromApi(importance: String): Importance {
        return when (importance) {
            "low" -> Importance.LOW
            "basic" -> Importance.NORMAL
            "important" -> Importance.HIGH
            else -> Importance.NORMAL
        }
    }

    private fun mapImportanceToApi(importance: Importance): String {
        return when (importance) {
            Importance.LOW -> "low"
            Importance.NORMAL -> "basic"
            Importance.HIGH -> "important"
        }
    }

    private fun parseColor(color: String?): Int {
        return try {
            if (color != null) {
                Color.parseColor(color)
            } else {
                Color.WHITE
            }
        } catch (e: Exception) {
            Color.WHITE
        }
    }

    private fun formatColor(color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }
}
