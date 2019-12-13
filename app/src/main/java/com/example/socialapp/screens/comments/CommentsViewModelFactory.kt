package com.example.socialapp.screens.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.socialapp.screens.userprofile.UserProfileViewModel

    class CommentsViewModelFactory(private val postId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
                return UserProfileViewModel(postId) as T
            }
            throw IllegalArgumentException("Unknown argument class")
        }
    }