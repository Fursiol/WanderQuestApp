package com.example.wanderquest.domain.model.cityevents


import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Result(
    val category: String,
    val description: String,
    val duration: Int,
    val end: String,
    val end_local: String,
    val entities: List<Entity>,
    val geo: Geo,
    val id: String,
    val labels: List<String>,
    val local_rank: Int,
    val location: List<Double>,
    val phq_attendance: Int,
    val phq_labels: List<PhqLabel>,
    val place_hierarchies: List<List<String>>,
    val predicted_end: String,
    val predicted_end_local: String,
    val predicted_event_spend: Int,
    val predicted_event_spend_industries: PredictedEventSpendIndustries,
    val rank: Int,
    val scope: String,
    val start: String,
    val start_local: String,
    val state: String,
    val timezone: String,
    val title: String
)

data class CityEvent(
    val id : String,
    val category: String,
    val description : String,
    val labels : List<String>,
    val title : String,
    val coordinates : LatLng,
    val startLocal : LocalDateTime,
    val endLocal : LocalDateTime,
    val dateParsed : String
)


fun Result.toDomain() : CityEvent {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    val start = this.start_local.format(formatter)
    val end = this.end_local.format(formatter)

    val startSplit = "$start".split("T")
    val startDate = startSplit[0].split("-")
    val startTime = startSplit[1].split(":")
    val startString = startDate[2].plus("/").plus(startDate[1]).plus("/").plus(startDate[0]).plus(" ").plus(startTime[0]).plus(":").plus(startTime[1])

    val endSplit = "$end".split("T")
    val endDate = endSplit[0].split("-")
    val endTime = endSplit[1].split(":")
    val endString = endDate[2].plus("/").plus(endDate[1]).plus("/").plus(endDate[0]).plus(" ").plus(endTime[0]).plus(":").plus(endTime[1])

    val categories = mapOf(
        "" to "",
        "school-holidays" to "Wakacje szkolne",
        "public-holidays" to "Święta państwowe",
        "observances" to "Obchody",
        "politics" to "Polityka",
        "conferences" to "Konferencje",
        "expos" to "Targi",
        "concerts" to "Koncerty",
        "festivals" to "Festiwale",
        "performing-arts" to "Sztuki występowe",
        "sports" to "Sporty",
        "community" to "Wspólnota",
        "daylight-savings" to "Czas letni",
        "airport-delays" to "Opóźnienia na lotniskach",
        "severe-weather" to "Ciężkie warunki pogodowe",
        "disasters" to "Katastrofy",
        "terror" to "Terror",
        "health-warnings" to "Ostrzeżenia zdrowotne",
        "academic" to "Akademicki"
    )


    return CityEvent(
        id = this.id,
        category = categories.get(this.category).toString(),
        description = this.description,
        labels = this.labels,
        title = title,
        coordinates = LatLng(
            this.geo.geometry.coordinates[1] as Double,
            this.geo.geometry.coordinates[0] as Double,
        ),
        startLocal = LocalDateTime.parse(this.start_local),
        endLocal = LocalDateTime.parse(this.start_local),

        dateParsed = "$startString  -  $endString"
    )
}