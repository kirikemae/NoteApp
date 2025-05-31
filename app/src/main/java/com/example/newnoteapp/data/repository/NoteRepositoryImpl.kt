package com.example.newnoteapp.data.repository

import android.util.Log
import com.example.newnoteapp.data.local.LocalDataSource
import com.example.newnoteapp.data.remote.RemoteDataSource
import com.example.newnoteapp.data.remote.NetworkException
import com.example.newnoteapp.domain.model.Note
import com.example.newnoteapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NoteRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : NoteRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _syncStatus = MutableStateFlow<String?>(null)
    override val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    companion object {
        private const val TAG = "NoteRepositoryImpl"
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return localDataSource.getAllNotes()
    }

    override fun getNoteById(id: String): Flow<Note?> {
        return localDataSource.getNoteById(id)
    }

    override suspend fun saveNote(note: Note) {
        try {
            Log.d(TAG, "Сохранение заметки: ${note.title}")
            _isLoading.value = true
            _syncStatus.value = "Сохранение заметки..."

            localDataSource.saveNote(note)
            Log.d(TAG, "Заметка сохранена локально")

            try {
                val savedNote = remoteDataSource.saveNote(note)
                localDataSource.saveNote(savedNote)
                _syncStatus.value = "Заметка синхронизирована"
                Log.d(TAG, "Заметка успешно синхронизирована с сервером")
            } catch (e: NetworkException.RevisionConflict) {
                Log.w(TAG, "Конфликт ревизий, пытаемся синхронизировать")
                _syncStatus.value = "Конфликт данных, синхронизация..."
                handleRevisionConflict(note)
            } catch (e: NetworkException.BadRequest) {
                Log.e(TAG, "Неверный запрос при синхронизации", e)
                _syncStatus.value = "Ошибка синхронизации: неверный запрос"
            } catch (e: NetworkException.Unauthorized) {
                Log.e(TAG, "Ошибка авторизации", e)
                _syncStatus.value = "Ошибка авторизации"
            } catch (e: NetworkException.ServerError) {
                Log.e(TAG, "Ошибка сервера", e)
                _syncStatus.value = "Ошибка сервера"
            } catch (e: Exception) {
                Log.e(TAG, "Неожиданная ошибка синхронизации", e)
                _syncStatus.value = "Ошибка синхронизации: ${e.message}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сохранении заметки", e)
            _syncStatus.value = "Ошибка сохранения: ${e.message}"
            throw e
        } finally {
            _isLoading.value = false
            clearStatusAfterDelay()
        }
    }

    override suspend fun deleteNote(id: String) {
        try {
            Log.d(TAG, "Удаление заметки: $id")
            _isLoading.value = true
            _syncStatus.value = "Удаление заметки..."

            localDataSource.deleteNote(id)
            Log.d(TAG, "Заметка удалена локально")

            try {
                remoteDataSource.deleteNote(id)
                _syncStatus.value = "Заметка удалена"
                Log.d(TAG, "Заметка успешно удалена с сервера")
            } catch (e: NetworkException.RevisionConflict) {
                Log.w(TAG, "Конфликт ревизий при удалении, пытаемся синхронизировать")
                _syncStatus.value = "Конфликт данных, синхронизация..."
                syncWithBackend()
            } catch (e: NetworkException.NotFound) {
                Log.w(TAG, "Заметка уже удалена с сервера")
                _syncStatus.value = "Заметка удалена"
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка удаления с сервера", e)
                _syncStatus.value = "Ошибка удаления: ${e.message}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при удалении заметки", e)
            _syncStatus.value = "Ошибка удаления: ${e.message}"
            throw e
        } finally {
            _isLoading.value = false
            clearStatusAfterDelay()
        }
    }

    override suspend fun syncWithBackend() {
        try {
            Log.d(TAG, "Начало синхронизации с бэкендом")
            _isLoading.value = true
            _syncStatus.value = "Синхронизация с сервером..."

            remoteDataSource.getAllNotes().collect { remoteNotes ->
                Log.d(TAG, "Получено ${remoteNotes.size} заметок с сервера")

                val localNotes = localDataSource.getAllNotes().first()
                Log.d(TAG, "Локально ${localNotes.size} заметок")

                localNotes.forEach { localDataSource.deleteNote(it.uid) }
                remoteNotes.forEach { localDataSource.saveNote(it) }

                _syncStatus.value = "Синхронизация завершена"
                Log.d(TAG, "Синхронизация успешно завершена")
            }
        } catch (e: NetworkException.Unauthorized) {
            Log.e(TAG, "Ошибка авторизации при синхронизации", e)
            _syncStatus.value = "Ошибка авторизации"
        } catch (e: NetworkException.ServerError) {
            Log.e(TAG, "Ошибка сервера при синхронизации", e)
            _syncStatus.value = "Ошибка сервера"
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка синхронизации", e)
            _syncStatus.value = "Ошибка синхронизации: ${e.message}"
        } finally {
            _isLoading.value = false
            clearStatusAfterDelay()
        }
    }

    private suspend fun handleRevisionConflict(note: Note) {
        try {
            Log.d(TAG, "Обработка конфликта ревизий для заметки: ${note.title}")

            syncWithBackend()

            val savedNote = remoteDataSource.saveNote(note)
            localDataSource.saveNote(savedNote)

            _syncStatus.value = "Конфликт разрешен, заметка сохранена"
            Log.d(TAG, "Конфликт ревизий успешно разрешен")
        } catch (e: Exception) {
            Log.e(TAG, "Не удалось разрешить конфликт ревизий", e)
            _syncStatus.value = "Не удалось разрешить конфликт: ${e.message}"
        }
    }

    private fun clearStatusAfterDelay() {
        repositoryScope.launch {
            kotlinx.coroutines.delay(3000)
            _syncStatus.value = null
        }
    }
}
