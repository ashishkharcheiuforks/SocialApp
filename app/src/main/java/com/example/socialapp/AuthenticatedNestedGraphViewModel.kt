package com.example.socialapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.socialapp.model.User
import com.example.socialapp.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class AuthenticatedNestedGraphViewModel : ViewModel(), KoinComponent {

    private val auth = FirebaseAuth.getInstance()
    private val repo: FirestoreRepository by inject()

    val user: LiveData<User> = repo.getUserLiveData(auth.uid!!)

    init {
        Timber.i("Init called")
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}