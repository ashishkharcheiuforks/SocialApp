package com.example.socialapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val firstName: String? = "",
    val nickname: String = "",
    val dateOfBirth: Timestamp? = null,
    val profilePictureUrl: String? = "",
    val aboutMe: String? = null
)