package com.example.wanderquest.domain.model.cityevents

import android.os.Build
import androidx.annotation.RequiresApi

data class CityEvents(
    val count: Int,
    val results: List<Result>
)


fun CityEvents.toDomain() : List<CityEvent> {

    val res = mutableListOf<CityEvent>()

    for (result in results)
    {
        if ( result.geo.geometry.type == "Point")
        {
            res.add(result.toDomain())
        }
    }
    return res
}


