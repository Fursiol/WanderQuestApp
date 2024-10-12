package com.example.wanderquest.view.admin.quests

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
import com.example.wanderquest.databinding.FragmentEditLocationBinding
import com.example.wanderquest.databinding.FragmentEditQuestBinding
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.Quest
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

private lateinit var locations: List<Location>
@AndroidEntryPoint
class EditLocationFragment : BaseFragment<FragmentEditLocationBinding>(
    FragmentEditLocationBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        binding.locationNameInputEditText.text = arguments?.getString("locationName")?.toEditable()
        binding.locationDescriptionInputEditText.text = arguments?.getString("locationDescription")?.toEditable()
        binding.locationLatInputEditText.text = arguments?.getString("locationLat")?.toEditable()
        binding.locationLngInputEditText.text = arguments?.getString("locationLng")?.toEditable()

        setToolBarOptions("Edycja lokacji", true)


        binding.popBackButton2.setOnClickListener {
            navController.popBackStack()
        }

        binding.submitEditLocationButton.setOnClickListener {

            val updatedLocation = Location(
                id = arguments?.getString("locationID").toString(),
                name = binding.locationNameInputEditText.text.toString(),
                desc = binding.locationDescriptionInputEditText.text.toString(),
                lat = binding.locationLatInputEditText.text.toString(),
                lng = binding.locationLngInputEditText.text.toString()
            )

            locationViewModel.updateLocation(updatedLocation) { result ->
                when (result) {
                    is ViewState.Success -> {
                        Toast.makeText(requireContext(), "Lokacja zmienione", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    is ViewState.Error -> {
                        Toast.makeText(requireContext(), "Błąd zmiany Lokacji: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        navController.popBackStack()
                    }
                }
            }
            navController.popBackStack()
        }

        binding.deleteLocationButton.setOnClickListener {
            locationViewModel.deleteLocation(arguments?.getString("locationID").toString()) {result ->
                when (result) {
                    is ViewState.Success -> {
                        Toast.makeText(requireContext(), "Lokacja usunięta", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    is ViewState.Error -> {
                        Toast.makeText(requireContext(), "Błąd podczas usuwania lokacji: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}