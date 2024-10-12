package com.example.wanderquest.view.admin.routes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.Quest
import com.example.wanderquest.domain.model.Route
import com.example.wanderquest.repository.FirebaseRepository
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) :  ViewModel() {

    private val _routes = MutableLiveData<ViewState<List<Route>>>()
    val routes: LiveData<ViewState<List<Route>>>
        get() = _routes

    fun createRoute(name : String, locationsIDs : List<Location>, onResult: (ViewState<String>) -> Unit){
        firebaseRepository.createRoute(name, locationsIDs, onResult)
    }

    fun loadRoutes(){
        _routes.postValue(ViewState.Loading)
        firebaseRepository.getAllRoutes { result ->
            _routes.postValue(result)
        }
    }

    fun updateRoute(route: Route, onResult: (ViewState<String>) -> Unit){
        firebaseRepository.updateRoute(route, onResult)
    }

    fun deleteRoute(routeID : String, onResult: (ViewState<String>) -> Unit){
        firebaseRepository.deleteRoute(routeID, onResult)
    }
}