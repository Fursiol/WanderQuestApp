package com.example.wanderquest.view.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderquest.repository.FirebaseRepository
import com.example.wanderquest.view.state.ViewState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) :  ViewModel() {

    private val _newLoggedInUser by lazy { MutableLiveData<ViewState<FirebaseUser>>() }
    val newLoggedInUser: LiveData<ViewState<FirebaseUser>>
        get() = _newLoggedInUser


    private val _currentUserData by lazy { MutableLiveData<ViewState<com.example.wanderquest.domain.model.User>>() }
    val currentUserData : LiveData<ViewState<com.example.wanderquest.domain.model.User>>
        get() = _currentUserData

    val loggedUser : FirebaseUser? = firebaseRepository.getCurrentUser()

    fun registerUserWihEmailAndPassword(name: String, email : String, password : String, admin : Boolean = false) {
        firebaseRepository.registerUserWihEmailAndPassword(name, email, password, admin, onResult = {res ->
            _newLoggedInUser.postValue(res)
        } )
    }

    fun logInWithEmailAndPassword(email: String, password: String) {
        firebaseRepository.logInWithEmailAndPassword(email, password, onResult =  {res ->
            _newLoggedInUser.postValue(res)
        })
    }

    fun getCurrentUserData() {
        _currentUserData.postValue(ViewState.Loading)
        firebaseRepository.getCurrentUserData( onResult =  {res ->
            _currentUserData.postValue(res)
        })
    }

}