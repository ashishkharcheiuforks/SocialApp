package com.example.socialapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.socialapp.model.User
import com.example.socialapp.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class AuthenticatedNestedGraphViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val repo = FirestoreRepository()

    val user: LiveData<User> = repo.getUserLiveData(auth.uid!!)

    init {
        Timber.i("Init called")
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}