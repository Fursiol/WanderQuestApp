package com.example.wanderquest.view.home.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wanderquest.R
import com.example.wanderquest.databinding.ItemCityEventBinding
import com.example.wanderquest.databinding.ItemWeatherForecastBinding
import com.example.wanderquest.domain.model.cityevents.CityEvent
import com.example.wanderquest.domain.model.weather.HourDto


class DiffUtilHourDto : DiffUtil.ItemCallback<HourDto>() {
    override fun areItemsTheSame(oldItem: HourDto, newItem: HourDto): Boolean {
        return oldItem.time == newItem.time
    }
    override fun areContentsTheSame(oldItem: HourDto, newItem: HourDto): Boolean {
        return oldItem == newItem
    }

}


class WeatherForecastAdapter :
    ListAdapter<HourDto, WeatherForecastAdapter.WeatherForecastViewHolder>(DiffUtilHourDto()) {

    var eventListClicked : ((HourDto) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherForecastViewHolder {
        val binding : ItemWeatherForecastBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_weather_forecast,
            parent,
            false
        )

        return WeatherForecastAdapter.WeatherForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherForecastViewHolder, position: Int) {
        var hourWeather = getItem(position)
        holder.binding.hourWeather = hourWeather



        Glide.with(holder.itemView.context)
            .load("https:${hourWeather.icon}")
            .into(holder.binding.ivWeatherHour)

    }


    class WeatherForecastViewHolder(val binding : ItemWeatherForecastBinding) : RecyclerView.ViewHolder(binding.root)


}
