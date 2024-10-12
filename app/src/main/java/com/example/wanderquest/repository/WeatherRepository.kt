package com.example.wanderquest.repository

import com.example.wanderquest.apiclient.IWeatherClient
import com.example.wanderquest.domain.model.cityevents.CityEvents
import com.example.wanderquest.domain.model.weather.Weather
import com.example.wanderquest.view.state.ViewState
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherClient : IWeatherClient) : BaseRepository() {

    suspend fun getWeather(q : String, days : Int) : ViewState<Weather>  {
        var result : ViewState<Weather>? = null

        try {
            val response = weatherClient.getTodayWeather("ef35e48b4fd64497911220535240806" ,q, days)
            if (response.isSuccessful) {
                result = handleSuccess(response.body()!!)
            }
            else {
                result = handleException(response.code())
            }

            return result
        }
        catch (e: Exception)
        {
            return ViewState.Error(e)
        }
    }
}