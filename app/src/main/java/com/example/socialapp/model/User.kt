package com.example.socialapp

import android.net.Uri
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String,
    val firstName: String = "",
    val nickname: String = "",
    val dateOfBirth: String? = "",
    val profilePictureUri: Uri,
    val aboutMe: String? = ""
)