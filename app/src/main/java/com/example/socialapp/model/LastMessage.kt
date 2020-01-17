package com.example.socialapp.model

import com.google.firebase.firestore.DocumentId

data class LastMessage(
    @DocumentId
    val chatRoomId: String = "",
    val user: User = User(),
    val message: Message = Message()
)