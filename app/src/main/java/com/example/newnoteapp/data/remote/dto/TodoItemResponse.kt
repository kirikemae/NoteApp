package com.example.newnoteapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TodoItemResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("element")
    val element: TodoItemDto,

    @SerializedName("revision")
    val revision: Int
)
