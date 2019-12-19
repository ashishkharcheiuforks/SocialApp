package com.example.socialapp.screens.editprofile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.FirestoreRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class EditProfileViewModel : ViewModel() {

    private val repo = FirestoreRepository()
    private val auth = FirebaseAuth.getInstance()

    val firstName = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val dateOfBirth = MutableLiveData<Timestamp>()
    val loadedImageUri = MutableLiveData<Uri?>()


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
            withContext(IO) {
                repo.updateUserProfileInfo(
                    firstName.value,
                    nickname.value,
                    dateOfBirth.value,
                    loadedImageUri.value
                )
            }
            loadedImageUri.value = null
        }

    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}