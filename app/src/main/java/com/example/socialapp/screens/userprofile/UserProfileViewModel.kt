package com.example.socialapp.screens.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialapp.DocumentSnapshotLiveData
import com.example.socialapp.User
import com.example.socialapp.UserInfoLiveData
import com.example.socialapp.screens.FirestoreRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class UserProfileViewmodel(private val uid: String) : ViewModel() {

    val userLiveData =
        UserInfoLiveData(
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
        )

    val user: LiveData<User>
        get() = userLiveData

    val friendshipStatus = getFriendshipStatus(uid)

    private val _friendshipButtonText = MutableLiveData<String>()
    val friendshipButtonText: LiveData<String>
        get() = _friendshipButtonText


    /**    Updates invite button label    */
    fun updateStatus(status: String?) {
        if (status != null) _friendshipButtonText.value = status
        else _friendshipButtonText.value = "Add to friends"
    }

    fun getFriendshipStatus(uid: String): DocumentSnapshotLiveData {
        return FirestoreRepository().getFriendshipStatus(uid)
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }

    fun inviteToFriends(): Task<Void> {
        return FirestoreRepository().inviteToFriends(uid)
    }

    fun acceptFriendRequest() {
        FirestoreRepository().acceptFriendRequest(uid)
    }

    fun cancelFriendRequest() {
        FirestoreRepository().deleteFriendRequest(uid)
    }

}
