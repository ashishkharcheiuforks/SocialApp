package com.example.socialapp

import androidx.lifecycle.ViewModel
import com.example.socialapp.livedata.AuthLiveData
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class MainActivityViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val authLiveData = AuthLiveData(auth)

}