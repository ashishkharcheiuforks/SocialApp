package com.example.socialapp

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivityViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()

    val authLiveData = FirebaseAuthLiveData(auth)

}