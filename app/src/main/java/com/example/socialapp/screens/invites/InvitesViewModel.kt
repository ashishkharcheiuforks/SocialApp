package com.example.socialapp.screens.invites

import androidx.lifecycle.ViewModel
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.livedata.QuerySnapshotLiveData
import timber.log.Timber

class InvitesViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    init {
        Timber.i("init called")
    }

    fun fetchInvitesLiveData(): QuerySnapshotLiveData {
        return repo.fetchInvitesLiveData()
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }

    fun acceptFriendRequest(uid: String) {
        Timber.d("acceptFriendRequest in viewmodel triggered for uid: $uid")
        repo.acceptFriendRequest(uid)
    }

    fun deleteFriendRequest(uid: String) {
        Timber.d("deleteFriendRequest in viewmodel triggered for uid: $uid")
        repo.deleteFriendRequest(uid)
    }
}