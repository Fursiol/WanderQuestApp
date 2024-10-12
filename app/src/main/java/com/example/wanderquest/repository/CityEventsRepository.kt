package com.example.wanderquest.repository

import com.example.wanderquest.apiclient.ICityEventsClient
import com.example.wanderquest.domain.model.cityevents.CityEvents
import com.example.wanderquest.view.state.ViewState
import retrofit2.Response
import javax.inject.Inject

class CityEventsRepository @Inject constructor(
    private val eventsClient: ICityEventsClient) : BaseRepository() {

    suspend fun getEvents(locationId : String, limit: Int, category: String ): ViewState<CityEvents> {
        var result : ViewState<CityEvents>? = null
        var response : Response<CityEvents>

        val limitValue: Int = if (limit <= 0)  10 else  limit

        if (category.isNullOrEmpty()) {
            response = eventsClient.getEvents(locationId, limit = limitValue)
        }
        else {
            response = eventsClient.getEvents(locationId, limit = limitValue, category = category)
        }

        if (response.isSuccessful) {
            result = handleSuccess(response.body()!!)
        }
        else {
            result = handleException(response.code())
        }

        return result
    }

}