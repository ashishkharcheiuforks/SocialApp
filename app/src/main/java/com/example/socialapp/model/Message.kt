package com.example.socialapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Message(
    @DocumentId
    val messageId: String = "",
    val text: String? = "",
    val dateCreated: Timestamp? = null,
    val createdByUserId: String = ""
)