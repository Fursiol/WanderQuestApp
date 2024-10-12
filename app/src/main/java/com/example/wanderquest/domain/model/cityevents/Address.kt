package com.example.wanderquest.domain.model.cityevents

data class Address(
    val country_code: String,
    val formatted_address: String,
    val locality: String,
    val postcode: String,
    val region: String
)