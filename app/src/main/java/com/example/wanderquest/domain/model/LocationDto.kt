package com.example.wanderquest.domain.model

import com.google.android.gms.maps.model.LatLng

class LocationDto {

    public var id: String = ""
    public var name: String = ""
    public var desc: String = ""
    public var lat: String = ""
    public var lng: String = ""
    public lateinit var quest : Quest
    public var done : Boolean = false

    public fun getLatLng() : LatLng {
        return LatLng(lat.toDouble(), lng.toDouble())
    }
}