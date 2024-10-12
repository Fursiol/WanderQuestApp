package com.example.wanderquest.view.home

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.wanderquest.databinding.FragmentWeatherBinding
import com.example.wanderquest.domain.model.weather.ForecastdayDto
import com.example.wanderquest.domain.model.weather.HourDto
import com.example.wanderquest.domain.model.weather.WeatherDto
import com.example.wanderquest.domain.model.weather.toDto
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.home.adapter.CityEventsAdapter
import com.example.wanderquest.view.home.adapter.WeatherForecastAdapter
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WeatherFragment : BaseFragment<FragmentWeatherBinding>
    (FragmentWeatherBinding::inflate) {

    private val adapter by lazy { WeatherForecastAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel.fetchWeather()
        observeWeatherData()
        initAdapter()
        setToolBarOptions("Pogoda", true, false);
    }

    fun observeWeatherData() {
        weatherViewModel.weather.observe(viewLifecycleOwner) {response ->
            when (response) {
                is ViewState.Success -> {

                    binding.weatherFragment.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE

                    val data = response.result.toDto()
                    handleListAdapter(data.forecast.forecastday[0].hour)
                    setUpViewData(data)
                }
                is ViewState.Error -> {

                }
                else -> {

                    binding.weatherFragment.visibility = View.GONE
                    binding.loading.visibility = View.VISIBLE
                }
            }
        }
    }

    fun initAdapter() {
        binding.rvForecast.adapter = adapter
    }
    fun handleListAdapter(list : List<HourDto>) {
        adapter.submitList(list)
    }

    fun setUpViewData(data : WeatherDto) {
        binding.tvLocation.text = data.location.name
        binding.tvTemperature.text = data.current.temp_c.toString() + "°C"

        binding.feelsLikeTextView.text = "Odczuwalnie: ${data.current.feelslike_c}°C"
        binding.windTextView.text = "Wiatr: ${data.current.wind_kph} km/h"
        binding.pressureTextView.text = "Ciśnienie: ${data.current.pressure_mb} hPa"
        binding.humidityTextView.text = "Wilgotność: ${data.current.humidity} %"
        binding.sunriseTextView.text = "Wschoód Słońca: ${data.forecast.forecastday[0].sunrise}"
        binding.sunsetTextView.text = "Zachód Słońca: ${data.forecast.forecastday[0].sunset}"

        Glide.with(requireContext())
            .load("https:${data.current.icon}")
            .into(binding.ivWeather)

    }
}