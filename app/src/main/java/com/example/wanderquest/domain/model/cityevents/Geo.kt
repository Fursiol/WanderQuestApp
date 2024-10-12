package com.example.wanderquest.domain.model.cityevents

data class Geo(
    val address: Address,
    val geometry: Geometry,
    val placekey: String
)