package com.example.wanderquest.domain.model

data class Quest(
    val locationID : String,
    val ID : String,
    val question : String,
    val correctAnswer : String,
    val ans1 : String,
    val ans2 : String,
    val ans3 : String
)
