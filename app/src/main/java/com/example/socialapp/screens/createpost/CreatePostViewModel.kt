package com.example.socialapp.screens.createpost

import android.net.Uri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialapp.FirestoreRepository
import timber.log.Timber

class CreatePostViewModel: ViewModel() {

    // Two-way data binding variables holding input fields of 'create new post' dialog
    // Text to be added in the post
    val postContent = MutableLiveData<String?>("")
    // Image to be uploaded with the post
    val postImage = MutableLiveData<Uri?>()
    // State of 'publish' button being enabled
    val publishButtonEnabled = MediatorLiveData<Boolean>()

    init {
        Timber.i("Init called")

        publishButtonEnabled.addSource(postContent) {
            publishButtonEnabled.value = validate()
        }
        publishButtonEnabled.addSource(postImage) {
            publishButtonEnabled.value = validate()
        }
    }

    // Checks if publish button in 'create new post' dialog should be enabled
    private fun validate(): Boolean {
        return if (postContent.value == null && postImage.value == null) {
            return false
        } else if (postContent.value != null && postImage.value == null) {
            return postContent.value!!.trim().isNotEmpty()
        } else if (postContent.value == null && postImage.value != null) {
            return true
        } else {
            false
        }
    }

    // Adds new post
    fun addPost() {
        Timber.i("addPost() called")
        FirestoreRepository()
            .addPost(postContent.value, postImage.value)
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared() called")
        publishButtonEnabled.removeSource(postImage)
        publishButtonEnabled.removeSource(postContent)
    }
}