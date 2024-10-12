package com.example.wanderquest.domain.model.weather

data class Location(
    val country: String,
    val lat: Double,
    val localtime: String,
    val localtime_epoch: Int,
    val lon: Double,
    val name: String,
    val region: String,
    val tz_id: String
)

data class LocationDto (
    val name : String,
    val country : String,
    val localtime : String
)

fun Location.toDto() : LocationDto {
    return LocationDto(
        name = this.name,
        country = this.country,
        localtime = this.localtime
    )
}