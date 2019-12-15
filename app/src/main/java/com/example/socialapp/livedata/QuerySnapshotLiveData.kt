package com.example.socialapp.livedata

import androidx.lifecycle.LiveData
import com.example.socialapp.common.Resource
import com.google.firebase.firestore.*
import timber.log.Timber


class QuerySnapshotLiveData(private val query: Query) : LiveData<Resource<QuerySnapshot>>(),
    EventListener<QuerySnapshot> {

    init {
        Timber.d("QuerySnapshotLiveData initialized")
    }

    private var registration: ListenerRegistration? = null

    override fun onEvent(snapshots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        Timber.d(snapshots!!.metadata.toString())
        value = if (e != null) {
            Resource(e)
        } else {
            Resource(snapshots)
        }
    }

    override fun onActive() {
        super.onActive()
        registration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()

        registration?.also {
            it.remove()
            registration = null
        }
    }
}