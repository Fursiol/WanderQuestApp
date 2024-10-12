package com.example.wanderquest.view.admin.locations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentAdminDashboardBinding
import com.example.wanderquest.databinding.FragmentManageLocationsBinding
import com.example.wanderquest.view.admin.locations.adapter.LocationAdapter
import com.example.wanderquest.view.admin.quests.adapter.QuestAdapter
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageLocationsFragment : BaseFragment<FragmentManageLocationsBinding>(
    FragmentManageLocationsBinding::inflate) {

    private lateinit var locationAdapter: LocationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setToolBarOptions("Zarządzanie lokacjami", true)

        binding.addLocationButton.setOnClickListener {
            navController.navigate(R.id.addLocationFragment)
        }

        locationAdapter = LocationAdapter()
        locationViewModel.loadLocations()

        binding.locationRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = locationAdapter
        }

        locationViewModel.locations.observe(viewLifecycleOwner) {viewState->
            when(viewState) {
                is ViewState.Loading -> {}
                is ViewState.Success -> {
                    val locationList = viewState.result
                    locationAdapter.submitList(locationList)
                }
                is ViewState.Error -> {
                    Toast.makeText(requireContext(), "Błąd: ${viewState.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        locationAdapter.locationEditClicked = { location ->
            navController.navigate(R.id.editLocationFragment, bundleOf(
                "locationID" to location.id,
                "locationName" to location.name,
                "locationDescription" to location.desc,
                "locationLat" to location.lat,
                "locationLng" to location.lng)
            )
        }
    }
}