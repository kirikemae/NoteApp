package com.example.newnoteapp.data.local.mapper

import com.example.newnoteapp.data.local.entity.NoteEntity
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.domain.model.Importance
import java.util.Date

object NoteEntityMapper {

    fun entityToNote(entity: NoteEntity): Note {
        return Note(
            uid = entity.uid,
            title = entity.title,
            content = entity.content,
            importance = Importance.valueOf(entity.importance),
            color = entity.color,
            createdAt = Date(entity.createdAt),
            modifiedAt = Date(entity.modifiedAt),
            selfDestructDate = entity.selfDestructDate
        )
    }

    fun noteToEntity(note: Note): NoteEntity {
        return NoteEntity(
            uid = note.uid,
            title = note.title,
            content = note.content,
            importance = note.importance.name,
            color = note.color,
            createdAt = note.createdAt.time,
            modifiedAt = note.modifiedAt.time,
            selfDestructDate = note.selfDestructDate
        )
    }

    fun entitiesToNotes(entities: List<NoteEntity>): List<Note> {
        return entities.map { entityToNote(it) }
    }

    fun notesToEntities(notes: List<Note>): List<NoteEntity> {
        return notes.map { noteToEntity(it) }
    }
}
