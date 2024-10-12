package com.example.wanderquest.view.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderquest.R
import com.example.wanderquest.databinding.ItemCityEventBinding
import com.example.wanderquest.domain.model.User
import com.example.wanderquest.domain.model.cityevents.CityEvent
import com.example.wanderquest.domain.model.cityevents.Result


class DiffUtilCityEvents : DiffUtil.ItemCallback<CityEvent>() {
    override fun areItemsTheSame(oldItem: CityEvent, newItem: CityEvent): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: CityEvent, newItem: CityEvent): Boolean {
        return oldItem == newItem
    }

}


class CityEventsAdapter :
    ListAdapter<CityEvent, CityEventsAdapter.CityEventsViewHolder>(DiffUtilCityEvents()) {

    var eventListClicked : ((CityEvent) -> Unit)? = null
    var eventDetailsClicked : ((CityEvent) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityEventsViewHolder {

        val binding : ItemCityEventBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_city_event,
            parent,
            false
        )

        return CityEventsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityEventsViewHolder, position: Int) {
        val event = getItem(position)
        holder.binding.event = event

        holder.binding.root.setOnClickListener {
            eventListClicked?.invoke(event)
        }

        holder.binding.detailsButton.setOnClickListener {
            eventDetailsClicked?.invoke(event)
        }

    }


    class CityEventsViewHolder(val binding : ItemCityEventBinding) : RecyclerView.ViewHolder(binding.root)

}










