package com.example.socialapp

import android.app.Application
import com.example.socialapp.koin.repoModule
import com.example.socialapp.koin.viewmodelsModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class SocialApplication : Application() {
    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())


        startKoin {
            androidLogger()

            androidContext(this@SocialApplication)

            androidFileProperties()

            modules(
                listOf(
                    viewmodelsModule,
                    repoModule
                )
            )
        }

    }
}