package com.example.wanderquest.repository

import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.Quest
import com.example.wanderquest.domain.model.Route
import com.example.wanderquest.view.state.ViewState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

interface IFirebaseRepository {
    fun getInstance(): FirebaseDatabase
    fun getCurrentUser() : FirebaseUser?
    fun registerUserWihEmailAndPassword(name: String, email: String, password: String, admin: Boolean, onResult: (ViewState<FirebaseUser>) -> Unit)
    fun logInWithEmailAndPassword(emailAddress: String, password: String, onResult: (ViewState<FirebaseUser>) -> Unit)
    fun createQuest(locationID : String, question : String, correctAnswer : String, ans1 : String, ans2 : String, ans3 : String, onResult: (ViewState<String>) -> Unit)
    fun getQuests(onResult: (ViewState<List<Quest>>) -> Unit)
    fun updateQuest(quest: Quest, onResult: (ViewState<String>) -> Unit)
    fun deleteQuest(questId: String, onResult: (ViewState<String>) -> Unit)
    fun createLocation(name : String, desc : String, Lat : String, Lng : String, onResult: (ViewState<String>) -> Unit)
    fun getLocations(onResult: (ViewState<List<Location>>) -> Unit)
    fun updateLocation(location: Location, onResult: (ViewState<String>) -> Unit)
    fun deleteLocation(locationID: String, onResult: (ViewState<String>) -> Unit)
    fun createRoute(name : String, locationsIDs : List<Location>, onResult: (ViewState<String>) -> Unit)
    fun updateRoute(location: Route, onResult: (ViewState<String>) -> Unit)
    fun deleteRoute(routeID: String, onResult: (ViewState<String>) -> Unit)
}