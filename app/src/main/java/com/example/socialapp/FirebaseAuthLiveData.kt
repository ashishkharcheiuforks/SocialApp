package com.example.socialapp

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthLiveData(private val auth: FirebaseAuth) : LiveData<FirebaseUser?>(),
    FirebaseAuth.AuthStateListener {
    private var lastUid: String? = null

    override fun onActive() {
        super.onActive()
        auth.addAuthStateListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        auth.removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        // only trigger when user changed
        if (lastUid != auth.currentUser?.uid) {
            lastUid = auth.currentUser?.uid
            value = auth.currentUser
        }
    }
}