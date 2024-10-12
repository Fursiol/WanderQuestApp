package com.example.wanderquest.view.admin.routes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderquest.R
import com.example.wanderquest.databinding.ItemQuestBinding
import com.example.wanderquest.databinding.ItemRouteBinding
import com.example.wanderquest.domain.model.Quest
import com.example.wanderquest.domain.model.Route

class DiffUtilRoute : DiffUtil.ItemCallback<Route>() {
    override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem.id  == newItem.id
    }

    override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem == newItem
    }
}

class RouteAdapter :
    ListAdapter<Route, RouteAdapter.RouteViewHolder>(DiffUtilRoute()) {

    var routeItemClicked: ((Route) -> Unit)? = null
    var routeEditClicked: ((Route) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding : ItemRouteBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_route,
            parent,
            false
        )
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = getItem(position)
        holder.binding.route = route
        holder.binding.questNameTextView.text = route.name

        holder.binding.root.setOnClickListener {
            routeItemClicked?.invoke(route)
        }

        holder.binding.routeEditButton.setOnClickListener {
            routeEditClicked?.invoke(route)
        }
    }
    class RouteViewHolder(val binding : ItemRouteBinding) : RecyclerView.ViewHolder(binding.root)
}