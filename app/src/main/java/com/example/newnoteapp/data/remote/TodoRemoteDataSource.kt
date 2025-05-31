package com.example.newnoteapp.data.remote

import android.util.Log
import com.example.newnoteapp.data.remote.api.TodoApi
import com.example.newnoteapp.data.remote.dto.TodoItemRequest
import com.example.newnoteapp.data.remote.mapper.TodoMapper
import com.example.newnoteapp.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class TodoRemoteDataSource(
    private val api: TodoApi
) : RemoteDataSource {

    private var currentRevision: Int = 0

    companion object {
        private const val TAG = "TodoRemoteDataSource"
    }

    override suspend fun getAllNotes(): Flow<List<Note>> = flow {
        try {
            Log.d(TAG, "Загрузка всех заметок с сервера")
            val response = api.getTodoList()
            if (response.isSuccessful) {
                val todoListResponse = response.body()
                if (todoListResponse != null) {
                    currentRevision = todoListResponse.revision
                    val notes = todoListResponse.list.map { TodoMapper.todoItemDtoToNote(it) }
                    Log.d(TAG, "Успешно загружено ${notes.size} заметок, ревизия: $currentRevision")
                    emit(notes)
                } else {
                    Log.e(TAG, "Пустой ответ от сервера")
                    throw Exception("Пустой ответ от сервера")
                }
            } else {
                Log.e(TAG, "Ошибка HTTP: ${response.code()} - ${response.message()}")
                handleHttpError(response.code(), response.message())
            }
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка сети при загрузке заметок", e)
            throw Exception("Ошибка сети: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP ошибка при загрузке заметок", e)
            throw Exception("Ошибка сервера: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Неожиданная ошибка при загрузке заметок", e)
            throw e
        }
    }

    override suspend fun getNoteById(id: String): Note? {
        return try {
            Log.d(TAG, "Загрузка заметки с ID: $id")
            val response = api.getTodoItem(id)
            if (response.isSuccessful) {
                val todoItemResponse = response.body()
                if (todoItemResponse != null) {
                    currentRevision = todoItemResponse.revision
                    val note = TodoMapper.todoItemDtoToNote(todoItemResponse.element)
                    Log.d(TAG, "Успешно загружена заметка: ${note.title}")
                    note
                } else {
                    Log.e(TAG, "Пустой ответ при загрузке заметки $id")
                    null
                }
            } else if (response.code() == 404) {
                Log.w(TAG, "Заметка с ID $id не найдена на сервере")
                null
            } else {
                Log.e(TAG, "Ошибка при загрузке заметки $id: ${response.code()}")
                handleHttpError(response.code(), response.message())
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка сети при загрузке заметки $id", e)
            throw Exception("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке заметки $id", e)
            throw e
        }
    }

    override suspend fun saveNote(note: Note): Note {
        return try {
            updateCurrentRevision()

            val todoItemDto = TodoMapper.noteToTodoItemDto(note)
            val request = TodoItemRequest(todoItemDto)

            Log.d(TAG, "Попытка обновления заметки: ${note.title} (ID: ${note.uid}) с ревизией: $currentRevision")
            val updateResponse = api.updateTodoItem(note.uid, currentRevision, request)

            if (updateResponse.isSuccessful) {
                val todoItemResponse = updateResponse.body()!!
                currentRevision = todoItemResponse.revision
                val updatedNote = TodoMapper.todoItemDtoToNote(todoItemResponse.element)
                Log.d(TAG, "Заметка успешно обновлена, новая ревизия: $currentRevision")
                updatedNote
            } else if (updateResponse.code() == 404) {
                Log.d(TAG, "Заметка не найдена, создаем новую с ревизией: $currentRevision")
                val addResponse = api.addTodoItem(currentRevision, request)
                if (addResponse.isSuccessful) {
                    val todoItemResponse = addResponse.body()!!
                    currentRevision = todoItemResponse.revision
                    val newNote = TodoMapper.todoItemDtoToNote(todoItemResponse.element)
                    Log.d(TAG, "Новая заметка успешно создана, новая ревизия: $currentRevision")
                    newNote
                } else if (addResponse.code() == 400) {
                    Log.w(TAG, "Конфликт ревизий при создании, обновляем ревизию и повторяем")
                    updateCurrentRevision()
                    val retryResponse = api.addTodoItem(currentRevision, request)
                    if (retryResponse.isSuccessful) {
                        val todoItemResponse = retryResponse.body()!!
                        currentRevision = todoItemResponse.revision
                        val newNote = TodoMapper.todoItemDtoToNote(todoItemResponse.element)
                        Log.d(TAG, "Заметка создана после повторной попытки, ревизия: $currentRevision")
                        newNote
                    } else {
                        Log.e(TAG, "Ошибка при повторном создании заметки: ${retryResponse.code()}")
                        handleHttpError(retryResponse.code(), retryResponse.message())
                        note
                    }
                } else {
                    Log.e(TAG, "Ошибка при создании заметки: ${addResponse.code()}")
                    handleHttpError(addResponse.code(), addResponse.message())
                    note
                }
            } else if (updateResponse.code() == 400) {
                Log.w(TAG, "Конфликт ревизий при обновлении, обновляем ревизию и повторяем")
                updateCurrentRevision()
                val retryResponse = api.updateTodoItem(note.uid, currentRevision, request)
                if (retryResponse.isSuccessful) {
                    val todoItemResponse = retryResponse.body()!!
                    currentRevision = todoItemResponse.revision
                    val updatedNote = TodoMapper.todoItemDtoToNote(todoItemResponse.element)
                    Log.d(TAG, "Заметка обновлена после повторной попытки, ревизия: $currentRevision")
                    updatedNote
                } else {
                    Log.e(TAG, "Ошибка при повторном обновлении заметки: ${retryResponse.code()}")
                    handleHttpError(retryResponse.code(), retryResponse.message())
                    note
                }
            } else {
                Log.e(TAG, "Ошибка при обновлении заметки: ${updateResponse.code()}")
                handleHttpError(updateResponse.code(), updateResponse.message())
                note
            }
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка сети при сохранении заметки", e)
            throw Exception("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при сохранении заметки", e)
            throw e
        }
    }

    override suspend fun deleteNote(id: String) {
        try {
            updateCurrentRevision()

            Log.d(TAG, "Удаление заметки с ID: $id, ревизия: $currentRevision")
            val response = api.deleteTodoItem(id, currentRevision)

            if (response.isSuccessful) {
                val todoItemResponse = response.body()
                if (todoItemResponse != null) {
                    currentRevision = todoItemResponse.revision
                    Log.d(TAG, "Заметка успешно удалена, новая ревизия: $currentRevision")
                }
            } else if (response.code() == 404) {
                Log.w(TAG, "Заметка с ID $id уже не существует на сервере")
            } else if (response.code() == 400) {
                // Конфликт ревизий при удалении
                Log.w(TAG, "Конфликт ревизий при удалении, обновляем ревизию и повторяем")
                updateCurrentRevision()
                val retryResponse = api.deleteTodoItem(id, currentRevision)
                if (retryResponse.isSuccessful) {
                    val todoItemResponse = retryResponse.body()
                    if (todoItemResponse != null) {
                        currentRevision = todoItemResponse.revision
                        Log.d(TAG, "Заметка удалена после повторной попытки, ревизия: $currentRevision")
                    }
                } else {
                    Log.e(TAG, "Ошибка при повторном удалении заметки $id: ${retryResponse.code()}")
                    handleHttpError(retryResponse.code(), retryResponse.message())
                }
            } else {
                Log.e(TAG, "Ошибка при удалении заметки $id: ${response.code()}")
                handleHttpError(response.code(), response.message())
            }
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка сети при удалении заметки $id", e)
            throw Exception("Ошибка сети: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при удалении заметки $id", e)
            throw e
        }
    }

    private suspend fun updateCurrentRevision() {
        try {
            Log.d(TAG, "Обновление текущей ревизии")
            val response = api.getTodoList()
            if (response.isSuccessful) {
                val todoListResponse = response.body()
                if (todoListResponse != null) {
                    val oldRevision = currentRevision
                    currentRevision = todoListResponse.revision
                    Log.d(TAG, "Ревизия обновлена с $oldRevision на $currentRevision")
                } else {
                    Log.w(TAG, "Пустой ответ при обновлении ревизии")
                }
            } else {
                Log.w(TAG, "Не удалось обновить ревизию: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Ошибка при обновлении ревизии", e)
        }
    }

    private fun handleHttpError(code: Int, message: String) {
        when (code) {
            400 -> {
                if (message.contains("unsynchronized", ignoreCase = true) ||
                    message.contains("revision", ignoreCase = true)) {
                    throw NetworkException.RevisionConflict
                } else {
                    throw NetworkException.BadRequest
                }
            }
            401 -> throw NetworkException.Unauthorized
            404 -> throw NetworkException.NotFound
            500 -> throw NetworkException.ServerError
            else -> throw NetworkException.ServerError
        }
    }
}
