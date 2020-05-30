package dev.example.ru.locationtracker

import android.app.Application

class LocationTrackerApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        AppDatabase.getInstance(applicationContext)
    }
}