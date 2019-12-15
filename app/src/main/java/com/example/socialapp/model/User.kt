package com.example.socialapp.model

import android.net.Uri
import com.google.firebase.Timestamp

data class User(
    val uid: String,
    val firstName: String = "",
    val nickname: String = "",
    val dateOfBirth: Timestamp? = null,
    val profilePictureUri: Uri,
    val aboutMe: String? = null
)