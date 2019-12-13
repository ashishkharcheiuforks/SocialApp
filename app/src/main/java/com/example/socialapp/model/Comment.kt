package com.example.socialapp.model

import com.google.firebase.Timestamp

data class Comment(
    val commentId: String,
    val content: String,
    val dateCreated: Timestamp,
    val user: User
)