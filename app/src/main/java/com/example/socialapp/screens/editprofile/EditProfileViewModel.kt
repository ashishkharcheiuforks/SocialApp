package com.example.socialapp.screens.editprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.algolia.search.saas.Client
import com.example.socialapp.screens.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import timber.log.Timber

class EditProfileViewmodel : ViewModel() {

    private val _loadedImageUri = MutableLiveData<Uri>()
    val loadedImageUri: LiveData<Uri>
        get() = _loadedImageUri

    init {
        Timber.i("Init called")
        _loadedImageUri.value = null
    }

    fun loadPicture(uri: Uri) {
        _loadedImageUri.value = uri
    }

    fun updateUserProfileInfo(
        firstName: String,
        nickname: String,
        dateOfBirth: String
    ) {
        FirestoreRepository().updateUserProfileInfo(
            firstName,
            nickname,
            dateOfBirth,
            _loadedImageUri.value
        )
        updateIndicesInAlgolia(firstName, nickname)
        _loadedImageUri.value = null
    }

    private fun updateIndicesInAlgolia(
        firstName: String,
        nickname: String
    ) {
        val apiKey = "02097ce2016d6a6f130949f04093678d"
        val applicationID = "78M3CITBN7"
        val client = Client(applicationID, apiKey)
        val index = client.getIndex("users")

        val jsonObject = JSONObject()
            .put("first_name", firstName)
            .put("nickname", nickname)

        index.partialUpdateObjectAsync(jsonObject, FirebaseAuth.getInstance().uid.toString(), null)
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}