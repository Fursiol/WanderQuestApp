package com.example.wanderquest.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseRepoModule {
    @Singleton
    @Provides
    fun provideFirebaseAuth() : FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    @Singleton
    @Provides
    fun provideFirebaseDatabase() : FirebaseDatabase {
        return  FirebaseDatabase.getInstance("https://wanderquest-9123b-default-rtdb.europe-west1.firebasedatabase.app/")
    }

}