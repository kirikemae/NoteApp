package com.example.newnoteapp.data.remote.api

import com.example.newnoteapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface TodoApi {

    @GET("list")
    suspend fun getTodoList(): Response<TodoListResponse>

    @GET("list/{id}")
    suspend fun getTodoItem(
        @Path("id") id: String
    ): Response<TodoItemResponse>

    @POST("list")
    suspend fun addTodoItem(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoItemRequest
    ): Response<TodoItemResponse>

    @PUT("list/{id}")
    suspend fun updateTodoItem(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: TodoItemRequest
    ): Response<TodoItemResponse>

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int
    ): Response<TodoItemResponse>
}
