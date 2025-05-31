package com.example.newnoteapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val uid: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "importance")
    val importance: String,

    @ColumnInfo(name = "color")
    val color: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "modified_at")
    val modifiedAt: Long,

    @ColumnInfo(name = "self_destruct_date")
    val selfDestructDate: Long? = null,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = false

)
