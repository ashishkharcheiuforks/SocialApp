package com.example.socialapp.screens.createpost

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.common.Result
import com.example.socialapp.repository.FirestoreRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class CreatePostViewModel : ViewModel() {

    // Two-way data binding variables holding input fields of 'create new post' dialog
    // Text to be added in the post
    val postContent = MutableLiveData<String?>("")
    val postImage = MutableLiveData<String?>(null)
    val publishButtonEnabled = MediatorLiveData<Boolean>()

    private val repo = FirestoreRepository()

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
        publishButtonEnabled.addSource(postContent) { validate() }
        publishButtonEnabled.addSource(postImage) { validate() }
    }

    // Called when onCleared is called on viewmodel destruction
    private fun removePublishButtonMediatorLiveDataSources() {
        publishButtonEnabled.removeSource(postImage)
        publishButtonEnabled.removeSource(postContent)
    }

    // Checks if publish button on toolbar in CreatePostDialogFragment should be enabled
    private fun validate() {
        publishButtonEnabled.value = !(postContent.value.isNullOrEmpty() && postImage.value == null)
    }

    // Uploads new post to the Firestore database
    fun addPost() {
        Timber.i("addPost() called")
        viewModelScope.launch {
            val addPostTaskResult =
                repo.addPost(postContent.value, postImage.value)

            when (addPostTaskResult) {
                is Result.Error -> {
                }
                is Result.Value -> {
                }
            }
        }
    }
}