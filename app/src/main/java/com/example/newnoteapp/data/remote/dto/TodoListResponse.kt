package com.example.newnoteapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TodoListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("list")
    val list: List<TodoItemDto>,

    @SerializedName("revision")
    val revision: Int
)
