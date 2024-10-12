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
import com.example.wanderquest.databinding.FragmentEditQuestBinding
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.Quest
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

private lateinit var locations: List<Location>
@AndroidEntryPoint
class EditQuestFragment : BaseFragment<FragmentEditQuestBinding>(
    FragmentEditQuestBinding::inflate) {

    var selectedLocation : String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        binding.questQuestionInputEditText.text = arguments?.getString("questQuestion")?.toEditable()
        binding.questCorrectAnswerInputEditText.text = arguments?.getString("questCorrectAnswer")?.toEditable()
        binding.questAnswer1InputEditText.text = arguments?.getString("questAns1")?.toEditable()
        binding.questAnswer2InputEditText.text = arguments?.getString("questAns2")?.toEditable()
        binding.questAnswer3InputEditText.text = arguments?.getString("questAns3")?.toEditable()

        setToolBarOptions("Edycja zadania", true)

        locationViewModel.loadLocations()

        locationViewModel.locations.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ViewState.Success -> {
                    locations = result.result
                    println(locations.get(0).name)
                    setupLocationSpinner(locations)
                }

                is ViewState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Błąd pobierania lokalizacji: ${result.exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> Unit
            }
        }

        binding.popBackButton2.setOnClickListener {
            navController.popBackStack()
        }

        binding.submitEditQuestButton.setOnClickListener {

            val updatedQuest = Quest(selectedLocation,
                arguments?.getString("questID").toString(),
                binding.questQuestionInputEditText.text.toString(),
                binding.questCorrectAnswerInputEditText.text.toString(),
                binding.questAnswer1InputEditText.text.toString(),
                binding.questAnswer2InputEditText.text.toString(),
                binding.questAnswer3InputEditText.text.toString())

            questViewModel.updateQuest(updatedQuest) { result ->
                when (result) {
                    is ViewState.Success -> {
                        Toast.makeText(requireContext(), "Zadanie zmienione", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    is ViewState.Error -> {
                        Toast.makeText(requireContext(), "Błąd zmiany zadania: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        navController.popBackStack()
                    }
                }
            }
            navController.popBackStack()
        }

        binding.deleteQuestButton.setOnClickListener {
            questViewModel.deleteQuest(arguments?.getString("questID").toString()) {result ->
                when (result) {
                    is ViewState.Success -> {
                        Toast.makeText(requireContext(), "Zadanie usunięte", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    is ViewState.Error -> {
                        Toast.makeText(requireContext(), "Błąd podczas usuwania zadania: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun setupLocationSpinner(locations: List<Location>) {
        val locationNames = locations.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locationNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSpinner.adapter = adapter
        binding.locationSpinner.setSelection(locations.indexOfFirst { it.id == arguments?.getString("locationID") })

        binding.locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLocation = locations[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}