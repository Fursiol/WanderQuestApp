package com.example.wanderquest.domain.model

data class Route(
    var id : String = "",
    var name : String = "",
    var locations : List<Location> = emptyList()
)
