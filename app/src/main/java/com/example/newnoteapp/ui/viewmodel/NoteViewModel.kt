package com.example.newnoteapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log

class NoteViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    val notes: StateFlow<List<Note>> = repository.getAllNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isLoading: StateFlow<Boolean> = repository.isLoading
    val syncStatus: StateFlow<String?> = repository.syncStatus

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val noteCache = mutableMapOf<String, StateFlow<Note?>>()

    companion object {
        private const val TAG = "NoteViewModel"
    }

    fun getNote(id: String): StateFlow<Note?> {
        return noteCache.getOrPut(id) {
            repository.getNoteById(id)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null
                )
        }
    }

    fun addOrUpdate(note: Note) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Сохранение заметки: ${note.title}")
                repository.saveNote(note)
                _errorMessage.value = null
                noteCache.remove(note.uid)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при сохранении заметки", e)
                _errorMessage.value = "Ошибка сохранения: ${e.message}"
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Удаление заметки: $id")
                repository.deleteNote(id)
                _errorMessage.value = null
                noteCache.remove(id)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при удалении заметки", e)
                _errorMessage.value = "Ошибка удаления: ${e.message}"
            }
        }
    }

    fun syncWithBackend() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Запуск синхронизации")
                repository.syncWithBackend()
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка синхронизации", e)
                _errorMessage.value = "Ошибка синхронизации: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        noteCache.clear()
    }
}
