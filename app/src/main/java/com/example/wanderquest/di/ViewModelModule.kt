package com.example.wanderquest.di

import com.example.wanderquest.repository.FirebaseRepository
import com.example.wanderquest.repository.IFirebaseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {
    @Binds
    @ViewModelScoped
    abstract fun firebaseAuthRepository(repo: FirebaseRepository) : IFirebaseRepository

}