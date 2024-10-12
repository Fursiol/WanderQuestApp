package com.example.wanderquest.view.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderquest.view.state.ViewState
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import  com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.LocationDto
import com.example.wanderquest.domain.model.Route
import com.example.wanderquest.repository.FirebaseRepository
import com.google.apphosting.datastore.testing.DatastoreTestTrace.FirestoreV1Action.ListDocuments
import javax.inject.Inject

@HiltViewModel
class RouteGameViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) :  ViewModel() {

    private val _locations  = MutableLiveData<ViewState<List<LocationDto>>>()
    val locations : LiveData<ViewState<List<LocationDto>>>
        get() = _locations

    private val _totalDistance = MutableLiveData<Float>(0f)
    val totalDistance: LiveData<Float>
        get() = _totalDistance

    fun getLocationsValues() : List<LocationDto>{
        when (locations.value){
            is ViewState.Success -> {
                return  (locations.value as ViewState.Success<List<LocationDto>>).result
            }
            else -> {
                return  emptyList<LocationDto>()
            }

        }
    }

    fun setLocationDone(id : String) {
        var loc = getLocationsValues()
        loc.find { it.id == id }?.let { location ->
            location.done = true
            _locations.postValue(ViewState.Success(loc))
        }
    }

    fun loadRouteLocations(locationsId : List<String>) {
        _locations.postValue(ViewState.Loading)
        firebaseRepository.getLocationsDto(locationsId) { result ->
            _locations.postValue(result)
        }
    }

    fun updateTotalDistance(distance: Float) {
        _totalDistance.value = (_totalDistance.value ?: 0f) + distance // Accumulate distance
    }

}