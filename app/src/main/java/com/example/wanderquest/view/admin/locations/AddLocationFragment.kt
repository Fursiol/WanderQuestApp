package com.example.wanderquest.view.admin.locations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentAddLocationBinding
import com.example.wanderquest.databinding.FragmentManageLocationsBinding
import com.example.wanderquest.di.FirebaseRepoModule
import com.example.wanderquest.repository.FirebaseRepository
import com.example.wanderquest.view.admin.locations.viewmodel.LocationViewModel
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

private lateinit var viewModel: LocationViewModel

@AndroidEntryPoint
class AddLocationFragment : BaseFragment<FragmentAddLocationBinding>(
    FragmentAddLocationBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setToolBarOptions("Dodawanie lokacji", true)

        binding.submitLocationButton.setOnClickListener {
            val locationName = binding.locationNameInputEditText.text.toString()
            val locationLat = binding.locationLatInputEditText.text.toString()
            val locationLng = binding.locationLngInputEditText.text.toString()
            val locationDesc = binding.locationDescInputEditText.text.toString()

            locationViewModel.createLocation(locationName, locationDesc, locationLat, locationLng) { result ->
                when (result) {
                    is ViewState.Success -> {
                        Toast.makeText(requireContext(), "Lokalizacja dodana: ${result.toString()}", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    is ViewState.Error -> {
                        Toast.makeText(requireContext(), "Błąd dodawania lokalizacji: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        binding.popBackButton1.setOnClickListener {
            navController.popBackStack()
        }

    }
}