package com.example.newnoteapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TodoItemRequest(
    @SerializedName("element")
    val element: TodoItemDto
)
