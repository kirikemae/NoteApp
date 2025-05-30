package com.example.newnoteapp.ui.navigate

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.ui.screens.*
import com.example.newnoteapp.ui.viewmodel.NoteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NoteNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: NoteViewModel = koinViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = "note_list"
    ) {
        composable("note_list") {
            NoteListRoute(
                onAddNote = {
                    navController.navigate("edit_note/new")
                },
                onNoteClick = { note ->
                    navController.navigate("edit_note/${note.uid}")
                },
                viewModel = viewModel
            )
        }

        composable("edit_note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")

            if (noteId == "new") {
                EditNoteScreen(
                    note = Note(title = "", content = ""),
                    onSave = { newNote ->
                        viewModel.addOrUpdate(newNote)
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            } else {
                EditNoteRoute(
                    noteId = noteId ?: "",
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }
    }
}
