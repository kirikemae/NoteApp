package com.example.newnoteapp.domain.model

import android.graphics.Color
import org.json.JSONObject
import java.util.UUID

enum class Importance {
    LOW, NORMAL, HIGH
}

data class Note(
    val uid: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val color: Int = Color.WHITE,
    val importance: Importance = Importance.NORMAL,
    val selfDestructDate: Long? = null
) {
    companion object {
        fun parse(json: JSONObject): Note? {
            return try {
                Note(
                    uid = json.optString("uid", UUID.randomUUID().toString()),
                    title = json.getString("title"),
                    content = json.getString("content"),
                    color = json.optInt("color", Color.WHITE),
                    importance = when (json.optString("importance")) {
                        "HIGH" -> Importance.HIGH
                        "LOW" -> Importance.LOW
                        else -> Importance.NORMAL
                    },
                    selfDestructDate = if (json.has("selfDestructDate")) {
                        json.optLong("selfDestructDate")
                    } else {
                        null
                    }
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    val json: JSONObject
        get() = JSONObject().apply {
            put("uid", uid)
            put("title", title)
            put("content", content)
            if (color != Color.WHITE) {
                put("color", color)
            }
            if (importance != Importance.NORMAL) {
                put("importance", importance.name)
            }
            selfDestructDate?.let {
                put("selfDestructDate", it)
            }
        }
}
