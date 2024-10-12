package com.example.wanderquest.domain.model

data class Answer(
    val id : String,
    val questionId : String,
    val text : String,
    val isCorrected : Boolean
)
