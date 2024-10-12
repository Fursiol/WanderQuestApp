package com.example.wanderquest.apiclient

import com.example.wanderquest.domain.model.cityevents.CityEvents
import  com.example.wanderquest.domain.model.cityevents.Result
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ICityEventsClient {
    @GET("/saved-locations/{location_id}/insights/events")
    suspend fun getEvents(@Path("location_id") locationId : String,
                          @Query("limit") limit : Int = 100,
                          @Query("category") category : String,
                          @Query("sort") sort : String = "start",
                          @Query("date_range_type") date_range_type : String = "next_90d",
                          ) : Response<CityEvents>


    @GET("/saved-locations/{location_id}/insights/events")
    suspend fun getEvents(@Path("location_id") locationId : String,
                          @Query("limit") limit : Int = 100,
                          @Query("sort") sort : String = "start",
                          @Query("date_range_type") date_range_type : String = "next_90d",
    ) : Response<CityEvents>
    @GET("/events")
    suspend fun getEvent(@Query("id") id : String) : Response<Result>

}