package com.example.newnoteapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.data.repository.NoteRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteViewModel(
    private val repository: NoteRepositoryImpl
) : ViewModel() {

    val notes: StateFlow<List<Note>> = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    init {
        syncWithBackend()
    }

    fun addOrUpdate(note: Note) {
        viewModelScope.launch {
            repository.addOrUpdate(note)
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    fun getNote(id: String) = repository.getNoteById(id)

    fun syncWithBackend() {
        viewModelScope.launch {
            _isLoading.value = true
            _syncStatus.value = "Синхронизация..."

            try {
                repository.syncWithBackend()
                _syncStatus.value = "Синхронизация завершена"
            } catch (e: Exception) {
                _syncStatus.value = "Ошибка синхронизации"
            } finally {
                _isLoading.value = false
                kotlinx.coroutines.delay(3000)
                _syncStatus.value = null
            }
        }
    }
}
