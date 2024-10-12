package com.example.wanderquest.view.admin.routes

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentAddQuestBinding
import com.example.wanderquest.databinding.FragmentAddRouteBinding
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRouteFragment : BaseFragment<FragmentAddRouteBinding>(
    FragmentAddRouteBinding::inflate) {

    private lateinit var selectedLocationIds: MutableList<String>
    private lateinit var selectedLocations: List<Location>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setToolBarOptions("Dodawanie trasy", true)

        locationViewModel.loadLocations()
        selectedLocationIds = mutableListOf()

        binding.popBackButton3.setOnClickListener {
            navController.popBackStack()
        }

        binding.selectLocationsButton.setOnClickListener {
            showLocationSelectionDialog()
        }

        binding.submitRouteButton.setOnClickListener {
            val routeName = binding.routeNameEditText.text.toString()
            if (routeName.isNotEmpty() && selectedLocationIds.isNotEmpty()) {
                routeViewModel.createRoute(routeName, selectedLocations) { result ->
                    when (result) {
                        is ViewState.Success -> {
                            Toast.makeText(requireContext(), "Dodano trasę", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        is ViewState.Error -> {
                            Toast.makeText(requireContext(), "Błąd dodawania trasy: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Proszę wypełnić wszystkie pola", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLocationSelectionDialog() {
        locationViewModel.locations.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ViewState.Success -> {
                    val locations = result.result
                    val locationIds = locations.map { it.id }.toTypedArray()
                    val locationNames = locations.map { it.name }.toTypedArray()
                    val selectedItems = BooleanArray(locations.size)

                    AlertDialog.Builder(requireContext())
                        .setTitle("Select Locations")
                        .setMultiChoiceItems(locationNames, selectedItems) { _, which, isChecked ->
                            if (isChecked) {
                                selectedLocationIds.add(locationIds[which])
                            } else {
                                selectedLocationIds.remove(locationIds[which])
                            }
                        }
                        .setPositiveButton("Akceptuj") { _, _ ->
                            selectedLocations = locations.filter { location ->
                                location.id in selectedLocationIds
                            }
                        }
                        .setNegativeButton("Anuluj", null)
                        .show()
                }
                is ViewState.Error -> {
                    Toast.makeText(requireContext(), "Błąd pobierania lokalizacji: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }
}