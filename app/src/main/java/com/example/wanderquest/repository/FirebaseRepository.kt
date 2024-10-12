package com.example.wanderquest.repository
import com.example.wanderquest.di.FirebaseRepoModule
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.LocationDto
import com.example.wanderquest.domain.model.Quest
import com.example.wanderquest.domain.model.Route
import com.example.wanderquest.domain.model.User
import com.example.wanderquest.view.state.ViewState
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import javax.inject.Inject

class FirebaseRepository
    @Inject constructor(
        private val auth: FirebaseAuth,
        private val database: FirebaseDatabase,
    )
    : IFirebaseRepository, BaseRepository() {
    override fun getInstance(): FirebaseDatabase {
       return database
    }
    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getCurrentUserData(onResult: (ViewState<User>) -> Unit) {
        val userReference = database.getReference(Entity.Users)

        if (auth.currentUser != null) {
            userReference.child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)

                        if (user != null) {
                            onResult(ViewState.Success(user))
                            return
                        }
                        onResult(ViewState.Error(java.lang.Exception("Nie znaleziono użytkownika")))
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(ViewState.Error(java.lang.Exception(error.message)))
                }

            })

        }
    }


    override fun registerUserWihEmailAndPassword(
        name: String,
        email: String,
        password: String,
        admin: Boolean,
        onResult: (ViewState<FirebaseUser>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {

                    try {
                        auth.currentUser?.let {user ->

                            val newUser = User(
                                id = user.uid,
                                name = name,
                                email = email,
                                admin = false
                            )

                            database.getReference(Entity.Users)
                                .child(newUser.id)
                                .setValue(newUser)

                            onResult(handleSuccess(user))
                        }
                    }
                    catch (e : Exception)
                    {
                        onResult(handleException(400))
                    }



                } else {
                    onResult(handleException(400))
                }
            }
    }

    override fun logInWithEmailAndPassword(
        emailAddress: String,
        password: String,
        onResult: (ViewState<FirebaseUser>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(emailAddress, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { user ->
                        onResult(handleSuccess(user))
                    }
                } else {
                    val exception = task.exception?.message
                    exception?.let {
                        if (it.contains("password")) {
                            onResult(handleException(403))
                        } else onResult(handleException(404))
                    }
                }
            }
    }

    override fun createQuest(
        locationID : String,
        question: String,
        correctAnswer: String,
        ans1: String,
        ans2: String,
        ans3: String,
        onResult: (ViewState<String>) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onResult(ViewState.Error(Exception("Użytkownik nie jest zalogowany")))
            return
        }

        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val questsRef = database.getReference("quests")

        val questData = mapOf(
            "locationID" to locationID,
            "question" to question,
            "correctAnswer" to correctAnswer,
            "ans1" to ans1,
            "ans2" to ans2,
            "ans3" to ans3
        )

        questsRef.push().setValue(questData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(ViewState.Success(questsRef.key ?: ""))
                } else {
                    onResult(ViewState.Error(Exception("Nie udało się utworzyć questa")))
                }
            }
            .addOnFailureListener { exception ->
                onResult(ViewState.Error(exception))
            }
    }

    override fun createLocation(
        name: String,
        desc: String,
        lat: String,
        lng: String,
        onResult: (ViewState<String>) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onResult(ViewState.Error(Exception("Użytkownik nie jest zalogowany")))
            return
        }

        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val locationRef = database.getReference("locations")

        val locationData = mapOf(
            "name" to name,
            "lat" to lat,
            "lng" to lng,
            "desc" to desc
        )

        locationRef.push().setValue(locationData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(ViewState.Success(locationRef.key ?: ""))
                } else {
                    onResult(ViewState.Error(Exception("Nie udało się dodać lokalizacji")))
                }
            }
            .addOnFailureListener { exception ->
                onResult(ViewState.Error(exception))
            }
    }

    override fun getLocations(onResult: (ViewState<List<Location>>) -> Unit) {
        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val locationRef = database.getReference("locations")

        locationRef.get().addOnSuccessListener { dataSnapshot ->
            val locations = mutableListOf<Location>()
            for (snapshot in dataSnapshot.children) {
                val id = snapshot.key
                val name = snapshot.child("name").getValue(String::class.java)
                val lat = snapshot.child("lat").getValue(String::class.java)
                val lng = snapshot.child("lng").getValue(String::class.java)
                val desc = snapshot.child("desc").getValue(String::class.java)

                if (id != null && name != null && desc != null && lat != null && lng != null) {
                    locations.add(Location(id, name, desc, lat, lng))
                } else {
                    null
                }
            }
            onResult(ViewState.Success(locations))
        }.addOnFailureListener { exception ->
            onResult(ViewState.Error(exception))
        }
    }

    fun getLocationsDto(locationsID: List<String>, onResult: (ViewState<MutableList<LocationDto>>) -> Unit) {
        val locationRef = database.getReference("locations")
        val questRef = database.getReference("quests") // Assuming this is the correct reference for quests

        locationRef.get().addOnSuccessListener { dataSnapshot ->
            val loc = mutableListOf<LocationDto>()
            val questTasks = mutableListOf<Task<Unit>>() // Store tasks for quests

            for (snapShot in dataSnapshot.children) {
                if (locationsID.contains(snapShot.key)) {
                    val locDto = LocationDto().apply {
                        id = snapShot.key.toString()
                        desc = snapShot.child("desc").getValue(String::class.java) ?: ""
                        lat = snapShot.child("lat").getValue(String::class.java) ?: ""
                        lng = snapShot.child("lng").getValue(String::class.java) ?: ""
                        name = snapShot.child("name").getValue(String::class.java) ?: ""
                    }

                    // Retrieve quests for the current location
                    val questTask = questRef.get().continueWith { questSnapshot ->
                        for (quest in questSnapshot.result.children) {
                            val locId = quest.child("locationID").getValue(String::class.java) ?: ""
                            if (locId == locDto.id) {
                                val id = quest.key
                                val locationID = quest.child("locationID").getValue(String::class.java)
                                val question = quest.child("question").getValue(String::class.java)
                                val correctAnswer = quest.child("correctAnswer").getValue(String::class.java)
                                val ans1 = quest.child("ans1").getValue(String::class.java)
                                val ans2 = quest.child("ans2").getValue(String::class.java)
                                val ans3 = quest.child("ans3").getValue(String::class.java)

                                if (id != null && question != null && locationID != null && correctAnswer != null
                                    && ans1 != null && ans2 != null && ans3 != null) {
                                    locDto.quest = Quest(locationID, id, question, correctAnswer, ans1, ans2, ans3)
                                }
                                break
                            }
                        }
                    }

                    // Add the task to the list
                    questTasks.add(questTask)
                    loc.add(locDto)
                }
            }

            // Wait for all quests to be retrieved before calling onResult
            Tasks.whenAllComplete(questTasks).addOnCompleteListener {
                onResult(ViewState.Success(loc))
            }
        }.addOnFailureListener { error ->
            onResult(ViewState.Error(error))
        }
    }



    override fun updateLocation(location: Location, onResult: (ViewState<String>) -> Unit) {
        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val locationRef = database.getReference("locations")

        locationRef.child(location.id).setValue(location)

        val routesRef = database.getReference("routes")
        routesRef.get().addOnSuccessListener { snapshot ->
            for (routeSnapshot in snapshot.children){
                val routeID = routeSnapshot.key
                val locationsMap = routeSnapshot.child("locations").value as? Map<String, Any>

                locationsMap?.let {locations ->
                    if (locations.containsKey((location.id))){
                        routesRef.child(routeID!!)
                            .child("locations")
                            .child(location.id)
                            .setValue(location)
                            .addOnSuccessListener {
                                onResult(ViewState.Success("Lokacja i trasy z nią - zaktualizowane."))
                            }
                    }
                }
            }
        }

    }

    override fun deleteLocation(locationID: String, onResult: (ViewState<String>) -> Unit) {
        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val locationRef = database.getReference("locations").child(locationID)

        locationRef.removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(ViewState.Success(locationID))
                } else {
                    onResult(ViewState.Error(Exception("Nie udało się usunąć lokacji")))
                }
            }
            .addOnFailureListener { exception ->
                onResult(ViewState.Error(exception))
            }
    }

    fun getAllRoutes(onResult: (ViewState<List<Route>>) -> Unit) {
        val routesRef = database.getReference("routes")

        routesRef.get().addOnSuccessListener { dataSnapshot ->
            val routes = mutableListOf<Route>()
            for (snapshot in dataSnapshot.children) {

               val name = snapshot.child("name").getValue(String::class.java)

                val loc = snapshot.child("locations").children
                val locationsList: MutableList<Location> = mutableListOf()

                for (l in loc) {
                    locationsList.add(
                        Location(
                            id = l.key.toString(),
                            name = l.child("name").getValue(String::class.java)!!,
                            desc = l.child("desc").getValue(String::class.java)!!,
                            lat = l.child("lat").getValue(String::class.java)!!,
                            lng = l.child("lng").getValue(String::class.java)!!
                        )
                    )
                }

                var route = Route(
                    id = snapshot.key.toString(),
                    name = name!!,
                    locations = locationsList.toList()
                )
                routes.add(route)

            }

            onResult(ViewState.Success(routes))
        }
            .addOnFailureListener {ex ->
                onResult(ViewState.Error(ex))
            }
    }

    override fun createRoute(
        name: String,
        locations: List<Location>,
        onResult: (ViewState<String>) -> Unit
    ) {
        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val routeRef = database.getReference("routes")

        val routeData = mapOf(
            "name" to name,
            "locations" to locations.associateBy { it.id }
        )

        routeRef.push().setValue(routeData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(ViewState.Success(routeRef.key ?: ""))
                } else {
                    onResult(ViewState.Error(Exception("Nie udało się dodać trasy")))
                }
            }
            .addOnFailureListener { exception ->
                onResult(ViewState.Error(exception))
            }
    }

    override fun getQuests(onResult: (ViewState<List<Quest>>) -> Unit) {
        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val questRef = database.getReference("quests")

        questRef.get().addOnSuccessListener { dataSnapshot ->
            val quests = mutableListOf<Quest>()
            for (snapshot in dataSnapshot.children) {
                val id = snapshot.key
                val locationID = snapshot.child("locationID").getValue(String::class.java)
                val question = snapshot.child("question").getValue(String::class.java)
                val correctAnswer = snapshot.child("correctAnswer").getValue(String::class.java)
                val ans1 = snapshot.child("ans1").getValue(String::class.java)
                val ans2 = snapshot.child("ans2").getValue(String::class.java)
                val ans3 = snapshot.child("ans3").getValue(String::class.java)

                if (id != null && question != null && locationID != null && correctAnswer != null
                    && ans1 != null && ans2 != null && ans3 != null) {
                    quests.add(Quest(locationID, id, question, correctAnswer, ans1, ans2, ans3))
                } else {
                    null
                }
            }
            onResult(ViewState.Success(quests))
        }.addOnFailureListener { exception ->
            onResult(ViewState.Error(exception))
        }
    }

    override fun updateQuest(quest: Quest, onResult: (ViewState<String>) -> Unit) {
        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val questRef = database.getReference("quests")

        questRef.child(quest.ID).setValue(quest)
    }

    override fun deleteQuest(questId: String, onResult: (ViewState<String>) -> Unit) {
        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val questRef = database.getReference("quests").child(questId)

        questRef.removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(ViewState.Success(questId))
                } else {
                    onResult(ViewState.Error(Exception("Nie udało się usunąć zadania")))
                }
            }
            .addOnFailureListener { exception ->
                onResult(ViewState.Error(exception))
            }
    }

    override fun deleteRoute(routeID: String, onResult: (ViewState<String>) -> Unit) {
        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val routeRef = database.getReference("routes").child(routeID)

        routeRef.removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(ViewState.Success(routeID))
                } else {
                    onResult(ViewState.Error(Exception("Nie udało się usunąć trasy")))
                }
            }
            .addOnFailureListener { exception ->
                onResult(ViewState.Error(exception))
            }
    }

    override fun updateRoute(route: Route, onResult: (ViewState<String>) -> Unit) {
        val database = FirebaseRepoModule.provideFirebaseDatabase()
        val questRef = database.getReference("routes")

        val routeData = mapOf(
            "name" to route.name,
            "locations" to route.locations.associateBy { it.id }
        )

        questRef.child(route.id).setValue(routeData)
    }
}