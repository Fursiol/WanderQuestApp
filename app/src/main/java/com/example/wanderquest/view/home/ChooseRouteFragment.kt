package com.example.wanderquest.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentChooseRouteBinding
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.home.adapter.ChooseRoutesAdapter
import com.example.wanderquest.view.home.adapter.CityEventsAdapter
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseRouteFragment : BaseFragment<FragmentChooseRouteBinding>
    (FragmentChooseRouteBinding::inflate) {

    private val adapter by lazy { ChooseRoutesAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading.visibility = View.GONE

        navController = findNavController()
        setToolBarOptions("Wybierz ścieżkę", false, false);
        initAdapter()

        routeViewModel.loadRoutes()

        routeViewModel.routes.observe(viewLifecycleOwner) {
            when (it)
            {
                is ViewState.Success -> {

                    adapter.submitList(it.result)
                    binding.loading.visibility = View.GONE

                }
                is ViewState.Error -> {
                }
                is ViewState.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initAdapter() {
        adapter.chooseButtonClicked = {route ->
            val r = route
            navController.navigate(R.id.action_chooseRouteFragment_to_routeGameFragment,
                bundleOf("routeId" to route.id, "locations" to ArrayList(route.locations))
                , null
            )
        }
        binding.rvRouteItems.adapter = adapter
    }
}