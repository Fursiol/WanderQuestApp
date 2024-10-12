package com.example.wanderquest.domain.model.weather

data class Weather(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)
data class WeatherDto(
    val current : CurrentDto,
    val forecast : ForecastDto,
    val location : LocationDto
)

fun Weather.toDto() : WeatherDto {
    return WeatherDto(
        current = this.current.toDto(),
        location = this.location.toDto(),
        forecast = this.forecast.toDto()
    )
}








