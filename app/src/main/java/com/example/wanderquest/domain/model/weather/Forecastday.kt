package com.example.wanderquest.domain.model.weather

data class Forecastday(
    val astro: Astro,
    val date: String,
    val date_epoch: Int,
    val day: Day,
    val hour: List<Hour>
)

data class ForecastdayDto (
    val sunrise : String,
    val sunset : String,
    val hour : List<HourDto>
)

fun Forecastday.toDto() : ForecastdayDto {
    return  ForecastdayDto (
        sunrise = this.astro.sunrise,
        sunset = this.astro.sunset,
        hour = this.hour.map {it.toDto()}
    )
}