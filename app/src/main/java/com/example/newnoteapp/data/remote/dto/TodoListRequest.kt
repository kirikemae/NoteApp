package com.example.newnoteapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TodoListRequest(
    @SerializedName("list")
    val list: List<TodoItemDto>
)
