package com.example.socialapp.livedata

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.socialapp.model.User
import com.google.firebase.firestore.*
import timber.log.Timber

class UserLiveData(
    private val docRef: DocumentReference
) : LiveData<User>(), EventListener<DocumentSnapshot> {

    private var listenerRegistration: ListenerRegistration? = null

    override fun onActive() {
        Timber.i("onActive() called")
        super.onActive()
        listenerRegistration = docRef.addSnapshotListener(this)
    }

    override fun onInactive() {
        Timber.i("onInactive() called")
        super.onInactive()
        listenerRegistration!!.remove()
    }

    override fun onEvent(snap: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (snap != null && snap.exists()) {
            Timber.d("onEvent() called ${snap.toString()}")
            val firstName = snap.getString("firstName").toString()
            val nickname = snap.getString("nickname").toString()
            val dateOfBirth = snap.getTimestamp("dateOfBirth")
            val profilePictureUri = Uri.parse(snap.getString("profilePictureUrl").toString())
            val aboutMe = snap.getString("aboutMe")

            val model = User(
                snap.id,
                firstName,
                nickname,
                dateOfBirth,
                profilePictureUri,
                aboutMe
            )
            value = model
        } else if (e != null) {
            Timber.w(e)
        }

    }
}