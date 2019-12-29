package com.example.socialapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Comment(
    @DocumentId
    val commentId: String = "",
    val content: String = "",
    val dateCreated: Timestamp? = null,
    val createdByUserId: String = ""
)