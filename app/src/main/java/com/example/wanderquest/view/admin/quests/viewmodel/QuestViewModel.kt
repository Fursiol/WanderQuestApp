package com.example.wanderquest.view.admin.quests.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.Quest
import com.example.wanderquest.repository.FirebaseRepository
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuestViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) :  ViewModel() {

    private val _quests = MutableLiveData<ViewState<List<Quest>>>()
    val quests: LiveData<ViewState<List<Quest>>> get() = _quests

    fun createQuest(locationID : String, question: String, correctAnswer : String, ans1 : String, ans2 : String, ans3 : String, onResult: (ViewState<String>) -> Unit){
        firebaseRepository.createQuest(locationID, question, correctAnswer, ans1, ans2, ans3, onResult)
    }

    fun loadQuests(){
        _quests.postValue(ViewState.Loading)
        firebaseRepository.getQuests { result ->
            _quests.postValue(result)
        }
    }

    fun updateQuest(quest: Quest, onResult: (ViewState<String>) -> Unit){
        firebaseRepository.updateQuest(quest, onResult)
    }

    fun deleteQuest(questID : String, onResult: (ViewState<String>) -> Unit){
        firebaseRepository.deleteQuest(questID, onResult)
    }
}