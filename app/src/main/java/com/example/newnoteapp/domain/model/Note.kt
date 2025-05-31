package com.example.newnoteapp.domain.model

import android.graphics.Color
import org.json.JSONObject
import java.util.UUID
import java.util.Date

enum class Importance {
    LOW, NORMAL, HIGH
}

data class Note(
    val uid: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val color: Int = Color.WHITE,
    val importance: Importance = Importance.NORMAL,
    val selfDestructDate: Long? = null,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date()
)