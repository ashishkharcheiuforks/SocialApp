package com.example.socialapp

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import timber.log.Timber

class DocumentSnapshotLiveData(
    private val docRef: DocumentReference
) : LiveData<Resource<DocumentSnapshot>>(), EventListener<DocumentSnapshot> {

    private var registration: ListenerRegistration? = null

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        // setValue
        Timber.i(snapshot!!.metadata.toString())
        value = if (e != null) {
            Resource(e)
        } else {
            Resource(snapshot!!)
        }
    }

    override fun onActive() {
        super.onActive()
        Timber.i("onActive() called")
        registration = docRef.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        Timber.i("onInactive() called")
        registration?.also {
            it.remove()
            registration = null
        }
    }
}