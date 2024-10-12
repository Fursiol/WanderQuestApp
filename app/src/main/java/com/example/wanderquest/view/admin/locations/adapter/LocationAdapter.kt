package com.example.wanderquest.view.admin.locations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderquest.R
import com.example.wanderquest.databinding.ItemLocationBinding
import com.example.wanderquest.databinding.ItemQuestBinding
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.Quest

class DiffUtilLocation : DiffUtil.ItemCallback<Location>() {
    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem == newItem
    }
}

class LocationAdapter :
    ListAdapter<Location, LocationAdapter.LocationViewHolder>(DiffUtilLocation()) {

    var locationItemClicked: ((Location) -> Unit)? = null
    var locationEditClicked: ((Location) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding : ItemLocationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_location,
            parent,
            false
        )
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.binding.location = location
        holder.binding.questNameTextView.text = location.name

        holder.binding.root.setOnClickListener {
            locationItemClicked?.invoke(location)
        }

        holder.binding.locationEditButton.setOnClickListener {
            locationEditClicked?.invoke(location)
        }
    }
    class LocationViewHolder(val binding : ItemLocationBinding) : RecyclerView.ViewHolder(binding.root)
}