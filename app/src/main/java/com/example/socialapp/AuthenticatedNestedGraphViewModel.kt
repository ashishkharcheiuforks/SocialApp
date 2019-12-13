package com.example.socialapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class AuthenticatedNestedGraphViewModel : ViewModel() {

    val userLiveData =
        UserInfoLiveData(
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().uid.toString())
        )

    val user: LiveData<User>
        get() = userLiveData

    init {
        Timber.i("Init called")
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }


}