package com.example.socialapp.screens.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserProfileViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileViewmodel::class.java)) {
            return UserProfileViewmodel(uid) as T
        }
        throw IllegalArgumentException("Unknown argument class")
    }
}