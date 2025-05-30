package com.example.newnoteapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.ui.components.NoteCard
import com.example.newnoteapp.ui.viewmodel.NoteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NoteListRoute(
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    viewModel: NoteViewModel = koinViewModel()
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()

    NoteListScreen(
        notes = notes,
        isLoading = isLoading,
        syncStatus = syncStatus,
        onAddNote = onAddNote,
        onNoteClick = onNoteClick,
        onDeleteNote = { note -> viewModel.delete(note.uid) },
        onSync = { viewModel.syncWithBackend() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    isLoading: Boolean,
    syncStatus: String?,
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заметки") },
                actions = {
                    IconButton(onClick = onSync) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Синхронизировать"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNote,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить заметку"
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            syncStatus?.let { status ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(text = status)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = notes,
                    key = { note -> note.uid }
                ) { note ->
                    SwipeToDeleteItem(
                        note = note,
                        onDelete = { onDeleteNote(note) },
                        onNoteClick = { onNoteClick(note) }
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeToDeleteItem(
    note: Note,
    onDelete: () -> Unit,
    onNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val color = when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Color.Red
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (swipeToDismissBoxState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                        Text(
                            text = "Delete",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        content = {
            NoteCard(
                note = note,
                onClick = onNoteClick,
                modifier = modifier
            )
        },
        modifier = modifier
    )
}
