package com.example.socialapp.model

import com.google.firebase.Timestamp

data class Message(val messageId: String?, val text: String, val dateCreated: Timestamp?, val sentByUserId: String?)