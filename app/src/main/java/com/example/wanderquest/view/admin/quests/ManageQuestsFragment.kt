package com.example.wanderquest.view.admin.quests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentManageLocationsBinding
import com.example.wanderquest.databinding.FragmentManageQuestsBinding
import com.example.wanderquest.view.admin.quests.adapter.QuestAdapter
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageQuestsFragment : BaseFragment<FragmentManageQuestsBinding>(
    FragmentManageQuestsBinding::inflate) {

    private lateinit var questAdapter: QuestAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setToolBarOptions("Zarządzanie zadaniami", true)

        questAdapter = QuestAdapter()

        binding.addQuestButton.setOnClickListener {
            navController.navigate(R.id.addQuestFragment)
        }

        binding.questsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = questAdapter
        }

        questViewModel.loadQuests()

        questViewModel.quests.observe(viewLifecycleOwner) {viewState->
            when(viewState) {
                is ViewState.Loading -> {}
                is ViewState.Success -> {
                    val questList = viewState.result
                    questAdapter.submitList(questList)
                }
                is ViewState.Error -> {
                    Toast.makeText(requireContext(), "Błąd: ${viewState.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        questAdapter.questEditClicked = { quest ->
            navController.navigate(R.id.editQuestFragment, bundleOf(
                "questID" to quest.ID,
                "locationID" to quest.locationID,
                "questQuestion" to quest.question,
                "questCorrectAnswer" to quest.correctAnswer,
                "questAns1" to quest.ans1,
                "questAns2" to quest.ans2,
                "questAns3" to quest.ans3))
        }
    }
}