package com.example.wanderquest.view.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanderquest.domain.model.cityevents.CityEvents
import com.example.wanderquest.domain.model.weather.Weather
import com.example.wanderquest.repository.WeatherRepository
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository : WeatherRepository
) : ViewModel() {

    private val _weather by lazy { MutableLiveData<ViewState<Weather>>() }
    val weather: LiveData<ViewState<Weather>>
        get() = _weather

    fun fetchWeather() {
        _weather.postValue(ViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val response = weatherRepository.getWeather("Poznan", 1)

            response.let {data ->
                withContext(Dispatchers.Main) {
                    when (data) {
                        is ViewState.Success -> {
                            _weather.postValue(data)
                        }
                        is ViewState.Error -> {
                            _weather.postValue(data)
                        }
                        else -> {
                            _weather.postValue(data)
                        }
                    }
                }

            }
        }
    }
}