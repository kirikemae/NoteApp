package com.example.newnoteapp

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.newnoteapp.domain.FileNotebook
import com.example.newnoteapp.domain.Note
import timber.log.Timber

@Composable
fun TestOperationsUI(context: Context, fileNotebook: FileNotebook) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            val loadedNotebook = FileNotebook.load(context)
            Timber.d("Loaded notebook with ${loadedNotebook.allNotes.size} notes")
        }) {
            Text("Load Notes")
        }

        Button(onClick = {
            val note = Note(
                uid = System.currentTimeMillis().toString(),
                title = "Test note",
                content = "This is a test",
                selfDestructDate = null
            )
            fileNotebook.addNote(note)
            Timber.d("Added note with uid ${note.uid}")
        }) {
            Text("Add Note")
        }

        Button(onClick = {
            if (fileNotebook.allNotes.isNotEmpty()) {
                val uid = fileNotebook.allNotes[0].uid
                fileNotebook.removeNote(uid)
                Timber.d("Removed note with uid $uid")
            }
        }) {
            Text("Remove Note")
        }

        Button(onClick = {
            val saved = fileNotebook.save(context)
            Timber.d("Saved notes: $saved")
        }) {
            Text("Save Notes")
        }

        Button(onClick = {
            val removedCount = fileNotebook.removeExpiredNotes()
            Timber.d("Removed $removedCount expired notes")
        }) {
            Text("Remove Expired Notes")
        }
    }
}
