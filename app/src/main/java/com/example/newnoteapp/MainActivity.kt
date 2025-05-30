package com.example.newnoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.newnoteapp.domain.Importance
import com.example.newnoteapp.domain.Note
import com.example.newnoteapp.ui.screens.EditNoteScreen
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exampleNote = Note(
            uid = UUID.randomUUID().toString(),
            title = "",
            content = "",
            importance = Importance.NORMAL,
            color = android.graphics.Color.WHITE,
            selfDestructDate = null
        )

        setContent {
            MaterialTheme {
                Surface {
                    EditNoteScreen(
                        note = exampleNote,
                        onSave = { note ->
                            println("Сохраняем заметку: $note")
                        },
                        onCancel = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}