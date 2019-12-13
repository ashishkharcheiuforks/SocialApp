package com.example.socialapp.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber

class FirebaseAuthLiveData(private val auth: FirebaseAuth) : LiveData<FirebaseUser?>(),
    FirebaseAuth.AuthStateListener {
    private var lastUid: String? = null

    override fun onActive() {
        Timber.i("onActive() called")
        super.onActive()
        auth.addAuthStateListener(this)
    }

    override fun onInactive() {
        Timber.i("onActive() called")
        super.onInactive()
        auth.removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        // only trigger when user changed
        Timber.i("onAuthStateChabged(auth) called")
        if (lastUid != auth.currentUser?.uid) {
            Timber.i("New value of FirebaseAuthLiveData set")
            lastUid = auth.currentUser?.uid
            value = auth.currentUser
        }
    }
}