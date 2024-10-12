package com.example.wanderquest.domain.model.weather

data class Forecast(
    val forecastday: List<Forecastday>
)

data class ForecastDto(
    val forecastday: List<ForecastdayDto>
)

fun Forecast.toDto() : ForecastDto {
    return ForecastDto(
        forecastday = this.forecastday.map { it.toDto() }
    )
}