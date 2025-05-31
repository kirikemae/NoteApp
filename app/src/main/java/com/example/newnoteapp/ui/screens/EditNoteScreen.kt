package com.example.newnoteapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.ui.components.*
import com.example.newnoteapp.ui.viewmodel.NoteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditNoteRoute(
    noteId: String,
    onNavigateBack: () -> Unit,
    viewModel: NoteViewModel = koinViewModel()
) {
    val noteFlow = viewModel.getNote(noteId)
    val note by noteFlow.collectAsStateWithLifecycle(initialValue = null)

    note?.let { currentNote ->
        EditNoteScreen(
            note = currentNote,
            onSave = { updatedNote ->
                viewModel.addOrUpdate(updatedNote)
                onNavigateBack()
            },
            onCancel = onNavigateBack
        )
    }
}

@Composable
fun EditNoteScreen(
    note: Note,
    onSave: (Note) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentNote by remember { mutableStateOf(note) }
    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NoteTitleEditor(
            title = currentNote.title,
            onTitleChange = { currentNote = currentNote.copy(title = it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        NoteContentEditor(
            content = currentNote.content,
            onContentChange = { currentNote = currentNote.copy(content = it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ImportanceSelector(
            importance = currentNote.importance,
            onImportanceChange = {
                currentNote = currentNote.copy(importance = it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ColorSelector(
            color = Color(currentNote.color),
            onColorClick = { showColorPicker = true },
            onPresetColorClick = { selectedColor ->
                currentNote = currentNote.copy(color = selectedColor.toArgb())
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelfDestructSelector(
            hasSelfDestruct = currentNote.selfDestructDate != null,
            selfDestructDate = currentNote.selfDestructDate,
            onSelfDestructChange = { enabled, date ->
                currentNote = currentNote.copy(
                    selfDestructDate = if (enabled) date else null
                )
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        ActionButtons(
            onSave = { onSave(currentNote) },
            onCancel = onCancel
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = Color(currentNote.color),
            onColorSelected = { color ->
                currentNote = currentNote.copy(color = color.toArgb())
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
}
