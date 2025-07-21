package com.example.miruni

import java.io.Serializable

data class Task(
    var title: String,
    var date: String,
    var content: String?,
    var status: String?
):Serializable
