package com.example.newnoteapp.data.remote

sealed class NetworkException(message: String) : Exception(message) {
    object NoInternetConnection : NetworkException("Нет подключения к интернету")
    object ServerError : NetworkException("Ошибка сервера")
    object Unauthorized : NetworkException("Ошибка авторизации")
    object NotFound : NetworkException("Ресурс не найден")
    object BadRequest : NetworkException("Неверный запрос")
    object RevisionConflict : NetworkException("Конфликт версий данных")
}
