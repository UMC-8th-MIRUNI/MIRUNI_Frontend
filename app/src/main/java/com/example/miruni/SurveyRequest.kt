package com.example.miruni

data class SurveyRequest(
    val situations: List<String>,
    val level: String,
    val reasons: List<String>
)