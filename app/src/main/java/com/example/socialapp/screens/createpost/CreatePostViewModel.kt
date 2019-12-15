package com.example.socialapp.screens.createpost

import android.net.Uri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialapp.FirestoreRepository
import timber.log.Timber

class CreatePostViewModel : ViewModel() {

    // Two-way data binding variables holding input fields of 'create new post' dialog
    // Text to be added in the post
    val postContent = MutableLiveData<String?>("")
    // Image to be uploaded with the post
    val postImage = MutableLiveData<Uri?>(null)
    // State of 'publish' button being enabled
    val publishButtonEnabled = MediatorLiveData<Boolean>()

    init {
        Timber.i("Init called")

        addPublishButtonMediatorLiveDataSources()
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        removePublishButtonMediatorLiveDataSources()
        super.onCleared()
    }

    // Called when viewmodel is initialized for the first time
    private fun addPublishButtonMediatorLiveDataSources() {
        publishButtonEnabled.addSource(postContent) {
            Timber.i("onchanged called : $it")
            publishButtonEnabled.value = validate()
        }
        publishButtonEnabled.addSource(postImage) {
            publishButtonEnabled.value = validate()
        }
    }

    // Called when onCleared is called on viewmodel destruction
    private fun removePublishButtonMediatorLiveDataSources() {
        publishButtonEnabled.removeSource(postImage)
        publishButtonEnabled.removeSource(postContent)
    }

    // Checks if publish button on toolbar in CreatePostDialogFragment should be enabled
    private fun validate(): Boolean {
        return !(postContent.value.isNullOrEmpty() && postImage.value == null)
    }

    // Uploads new post to the Firestore database
    fun addPost() {
        Timber.i("addPost() called")
        FirestoreRepository()
            .addPost(postContent.value, postImage.value)
    }
}