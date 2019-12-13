package com.example.socialapp

import android.app.Application
import timber.log.Timber

class SocialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}