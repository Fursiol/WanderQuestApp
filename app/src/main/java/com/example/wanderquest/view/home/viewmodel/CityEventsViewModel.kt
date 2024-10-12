package com.example.wanderquest.view.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderquest.domain.model.cityevents.CityEvents
import com.example.wanderquest.repository.CityEventsRepository
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CityEventsViewModel @Inject constructor(
    private val cityEventsRepository: CityEventsRepository
) : ViewModel() {

    private val _events by lazy { MutableLiveData<ViewState<CityEvents>>() }
    val events: LiveData<ViewState<CityEvents>>
        get() = _events

    /// filtry
    private val _limit = MutableLiveData<Int>().apply {
        value = 10
    }
    val limit: LiveData<Int>
        get() = _limit

    private val _category = MutableLiveData<String>().apply {
        value = ""
    }
    val category : LiveData<String>
        get() = _category

    val categories = mapOf(
        "" to "Wszystkie",
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

     fun fetchCityEvents(locationId : String) {
         _events.postValue(ViewState.Loading)
         viewModelScope.launch(Dispatchers.IO) {
             var response : ViewState<CityEvents>? = null
             response = cityEventsRepository.getEvents(locationId, limit.value!!, category.value!!)

             response.let {data ->
                 withContext(Dispatchers.Main) {
                     when (data) {
                         is ViewState.Success -> {
                             _events.postValue(data)
                         }
                         is ViewState.Error -> {
                             _events.postValue(data)
                         }
                         else -> {
                             _events.postValue(data)
                         }
                     }
                 }

             }
         }
     }

    fun setLimit (limit : Int) {
        _limit.value = limit
    }

    fun setCategory(category : String) {
        _category.value = category
    }
}