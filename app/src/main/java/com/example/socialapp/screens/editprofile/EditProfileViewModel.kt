package com.example.socialapp.screens.editprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.algolia.search.saas.Client
import com.example.socialapp.FirestoreRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

class EditProfileViewModel : ViewModel() {

    private val _loadedImageUri = MutableLiveData<Uri>()
    val loadedImageUri: LiveData<Uri>
        get() = _loadedImageUri

    private val _dateOfBirth = MutableLiveData<Timestamp>()
    val dateOfBirth: LiveData<Timestamp>
        get() = _dateOfBirth



    init {
        Timber.i("Init called")
    }

    fun loadPicture(uri: Uri) {
        _loadedImageUri.value = uri
    }

    fun setDate(date: Timestamp){
        _dateOfBirth.value = date
    }

    suspend fun updateUserProfileInfo(
        firstName: String,
        nickname: String
    ) {
        FirestoreRepository().updateUserProfileInfo(
            firstName,
            nickname,
            _dateOfBirth.value,
            _loadedImageUri.value
        )

        withContext(Dispatchers.Main){
        _loadedImageUri.value = null
        }
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}