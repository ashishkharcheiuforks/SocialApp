package com.example.socialapp.screens.chat

import androidx.lifecycle.ViewModel
import timber.log.Timber

class ConversationViewModel(userId: String) : ViewModel() {
    init {
        Timber.i("init called")
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}
