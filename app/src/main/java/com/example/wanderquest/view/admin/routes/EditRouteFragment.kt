package com.example.wanderquest.view.admin.routes

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentAddQuestBinding
import com.example.wanderquest.databinding.FragmentEditQuestBinding
import com.example.wanderquest.databinding.FragmentEditRouteBinding
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.Quest
import com.example.wanderquest.domain.model.Route
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

private lateinit var selectedLocations: List<Location>
private lateinit var selectedLocationIds: MutableList<String>
@AndroidEntryPoint
class EditRouteFragment : BaseFragment<FragmentEditRouteBinding>(
    FragmentEditRouteBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        binding.routeNameEditText.text = arguments?.getString("routeName")?.toEditable()
        val locations = arguments?.getParcelableArrayList<Location>("locations") as List<Location>

        val loc_id_list = mutableListOf<String>()
        for(loc in locations){
            loc_id_list.add(loc.id)
        }

        selectedLocationIds = loc_id_list

        setToolBarOptions("Edycja zadania", true)

        locationViewModel.loadLocations()

        binding.popBackButton3.setOnClickListener {
            navController.popBackStack()
        }

        binding.selectLocationsButton.setOnClickListener {
            showLocationSelectionDialog()
        }

        binding.submitEditRouteButton.setOnClickListener {

            val updatedRoute = Route(
                id = arguments?.getString("routeID").toString(),
                name = arguments?.getString("routeName").toString(),
                locations = selectedLocations
            )

            routeViewModel.updateRoute(updatedRoute) { result ->
                when (result) {
                    is ViewState.Success -> {
                        Toast.makeText(requireContext(), "Trasa zmienione", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    is ViewState.Error -> {
                        Toast.makeText(requireContext(), "Błąd zmiany trasy: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        navController.popBackStack()
                    }
                }
            }
            navController.popBackStack()
        }

        binding.deleteRouteButton.setOnClickListener {
            routeViewModel.deleteRoute(arguments?.getString("routeID").toString()) {result ->
                when (result) {
                    is ViewState.Success -> {
                        Toast.makeText(requireContext(), "Trasa usunięta", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    is ViewState.Error -> {
                        Toast.makeText(requireContext(), "Błąd podczas usuwania trasy: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                    }
                }
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
                    val selectedItems = BooleanArray(locations.size) { index ->
                        locationIds[index] in selectedLocationIds
                    }

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
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}