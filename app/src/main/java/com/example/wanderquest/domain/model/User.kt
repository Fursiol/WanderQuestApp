package com.example.wanderquest.domain.model

data class User(
    val id: String = "",
    var name : String = "",
    var email : String= "",
    var admin : Boolean = false
)
