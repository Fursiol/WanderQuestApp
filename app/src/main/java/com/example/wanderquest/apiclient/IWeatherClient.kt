package com.example.wanderquest.apiclient

import com.example.wanderquest.domain.model.weather.Weather
import com.example.wanderquest.view.state.ViewState
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IWeatherClient  {
    @GET("/v1/forecast.json")
    suspend fun getTodayWeather(@Query("key") key : String, @Query("q") q: String, @Query("days") days : Int) : Response<Weather>
}