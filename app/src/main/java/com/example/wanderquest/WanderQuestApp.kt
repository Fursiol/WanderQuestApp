package com.example.wanderquest

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WanderQuestApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}