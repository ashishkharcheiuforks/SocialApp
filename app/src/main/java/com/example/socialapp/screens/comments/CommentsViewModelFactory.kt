package com.example.socialapp.screens.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CommentsViewModelFactory(private val postId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            return CommentsViewModel(postId) as T
        }
        throw IllegalArgumentException("Unknown argument class")
    }
}