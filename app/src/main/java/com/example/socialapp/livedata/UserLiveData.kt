package com.example.socialapp.livedata

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
            value = snap.toObject(User::class.java)
        } else if (e != null) {
            Timber.w(e)
        }

    }
}