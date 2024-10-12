package com.example.wanderquest.view.admin.locations.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.Quest
import com.example.wanderquest.repository.FirebaseRepository
import com.example.wanderquest.view.state.ViewState
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) :  ViewModel() {

    private val _locations = MutableLiveData<ViewState<List<Location>>>()
    val locations: LiveData<ViewState<List<Location>>> get() = _locations
    fun createLocation(name: String, desc: String, lat: String, lng: String, onResult: (ViewState<String>) -> Unit) {
        firebaseRepository.createLocation(name, desc, lat, lng, onResult)
    }

    fun loadLocations(){
        _locations.postValue(ViewState.Loading)
        firebaseRepository.getLocations { result ->
            _locations.postValue(result)
        }
    }

    fun updateLocation(location: Location, onResult: (ViewState<String>) -> Unit){
        firebaseRepository.updateLocation(location, onResult)
    }

    fun deleteLocation(locationID : String, onResult: (ViewState<String>) -> Unit){
        firebaseRepository.deleteLocation(locationID, onResult)
    }
}