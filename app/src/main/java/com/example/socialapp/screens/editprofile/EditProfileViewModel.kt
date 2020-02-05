package com.example.socialapp.screens.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.common.Result
import com.example.socialapp.livedata.SingleLiveEvent
import com.example.socialapp.repository.FirestoreRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import timber.log.Timber

class EditProfileViewModel : ViewModel() {

    private val repo = FirestoreRepository()
    private val auth = FirebaseAuth.getInstance()

    val firstName = MutableLiveData<String>("")
    val nickname = MutableLiveData<String>("")
    val dateOfBirth = MutableLiveData<Timestamp>()
    val loadedImageUri = MutableLiveData<String?>("")

    private val _updateFinishedMessage = SingleLiveEvent<String>()
    val updateFinishedMessage: LiveData<String>
        get() = _updateFinishedMessage

    init {
        Timber.i("Init called")
        viewModelScope.launch {
            val user = repo.getUser(auth.uid!!)
            firstName.value = user.firstName
            nickname.value = user.nickname
            dateOfBirth.value = user.dateOfBirth
            loadedImageUri.value = null
        }
    }

    fun updateUserProfileInfo() {
        viewModelScope.launch {
            val updateTaskResult = repo.updateUserProfileInfo(
                firstName.value,
                nickname.value,
                dateOfBirth.value,
                loadedImageUri.value,
                null
            )
            when (updateTaskResult) {
                is Result.Error -> {
                    _updateFinishedMessage.value = "Failed to save changes. Try again"
                }
                is Result.Value -> {
                    _updateFinishedMessage.value = "Changes saved"
                }
            }

        }
        loadedImageUri.value = null
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}