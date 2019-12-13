package com.example.socialapp.screens.friends

import androidx.lifecycle.ViewModel
import timber.log.Timber

class FriendsViewModel : ViewModel() {

    init {
        Timber.i("init called")
    }

    //Data source and livedata<pagedlist<>>

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared() called")
    }
}