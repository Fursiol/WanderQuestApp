package com.example.wanderquest.view.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderquest.R
import com.example.wanderquest.databinding.ItemChooseRouteBinding
import com.example.wanderquest.databinding.ItemCityEventBinding
import com.example.wanderquest.databinding.ItemWeatherForecastBinding
import com.example.wanderquest.domain.model.Route
import com.example.wanderquest.domain.model.cityevents.CityEvent

class DiffUtilRoute : DiffUtil.ItemCallback<Route>() {
    override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem == newItem
    }
}

class ChooseRoutesAdapter : ListAdapter<Route, ChooseRoutesAdapter.ChooseRoutesViewHolder>(DiffUtilRoute()) {

    var chooseButtonClicked : ((Route) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseRoutesViewHolder {
        val binding : ItemChooseRouteBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_choose_route,
            parent,
            false
        )

        return ChooseRoutesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseRoutesViewHolder, position: Int) {
        val route = getItem(position)
        holder.binding.route = route

        holder.binding.chooseButton.setOnClickListener{
            chooseButtonClicked?.invoke(route);
        }

    }
    class ChooseRoutesViewHolder(val binding : ItemChooseRouteBinding) : RecyclerView.ViewHolder(binding.root)
}
