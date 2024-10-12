package com.example.wanderquest.view.admin.quests

import android.os.Bundle
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
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

private  var locations: List<Location> = mutableListOf()
@AndroidEntryPoint
class AddQuestFragment : BaseFragment<FragmentAddQuestBinding>(
    FragmentAddQuestBinding::inflate) {

    var selectedLocation : String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setToolBarOptions("Dodawanie zadania", true)

        locationViewModel.loadLocations()

        locationViewModel.locations.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ViewState.Success -> {
                    locations = result.result
                    println(locations.get(0).name)
                    setupLocationSpinner(locations)
                }
                is ViewState.Error -> {
                    Toast.makeText(requireContext(), "Błąd pobierania lokalizacji: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        binding.submitQuestButton.setOnClickListener {

            val questQuestion = binding.questQuestionInputEditText.text.toString()
            val questCorrectAnswer = binding.questCorrectAnswerInputEditText.text.toString()
            val questAnswer1 = binding.questAnswer1InputEditText.text.toString()
            val questAnswer2 = binding.questAnswer2InputEditText.text.toString()
            val questAnswer3 = binding.questAnswer3InputEditText.text.toString()

            questViewModel.createQuest(selectedLocation, questQuestion, questCorrectAnswer, questAnswer1, questAnswer2, questAnswer3) { result ->
                when (result) {
                    is ViewState.Success -> {
                        Toast.makeText(requireContext(), "Zadanie dodane: ${result.toString()}", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    is ViewState.Error -> {
                        Toast.makeText(requireContext(), "Błąd dodawania zadania: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


        binding.popBackButton2.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun setupLocationSpinner(locations: List<Location>) {
        val locationNames = locations.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locationNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSpinner.adapter = adapter

        binding.locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLocation = locations[position].id

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}