package com.example.newnoteapp.ui.navigate

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newnoteapp.domain.Note
import com.example.newnoteapp.ui.screens.*

@Composable
fun NoteNavigation(
    navController: NavHostController = rememberNavController()
) {
    val notes = remember { mutableStateListOf<Note>() }

    LaunchedEffect(Unit) {
        if (notes.isEmpty()) {
            notes.addAll(listOf(
                Note(
                    title = "Покупки",
                    content = "Молоко, хлеб, яйца",
                    importance = com.example.newnoteapp.domain.Importance.HIGH
                ),
                Note(
                    title = "Встреча",
                    content = "Встреча с коллегой в 15:00",
                    color = android.graphics.Color.YELLOW
                )
            ))
        }
    }

    NavHost(
        navController = navController,
        startDestination = "note_list"
    ) {
        composable("note_list") {
            NoteListScreen(
                notes = notes,
                onAddNote = {
                    navController.navigate("edit_note/new")
                },
                onNoteClick = { note ->
                    navController.navigate("edit_note/${note.uid}")
                },
                onDeleteNote = { noteToDelete ->
                    notes.removeIf { it.uid == noteToDelete.uid }
                }
            )
        }

        composable("edit_note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            val currentNote = if (noteId == "new") {
                Note(title = "", content = "")
            } else {
                notes.find { it.uid == noteId } ?: Note(title = "", content = "")
            }

            EditNoteScreen(
                note = currentNote,
                onSave = { updatedNote ->
                    if (noteId == "new") {
                        notes.add(updatedNote)
                    } else {
                        val index = notes.indexOfFirst { it.uid == updatedNote.uid }
                        if (index != -1) notes[index] = updatedNote
                    }
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}
