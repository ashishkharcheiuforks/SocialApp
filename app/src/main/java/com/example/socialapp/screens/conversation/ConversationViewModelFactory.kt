package com.example.socialapp.screens.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ConversationViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    @ExperimentalCoroutinesApi
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
            return ConversationViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown argument class")
    }
}