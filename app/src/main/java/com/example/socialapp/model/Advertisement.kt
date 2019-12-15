package com.example.socialapp.model

import com.google.firebase.Timestamp

data class Advertisement(
    val advertisementId: String?,
    val filters: Filters,
    val description: String? = null,
    val createdByUserUid: String? = null,
    val user: User?,
    val dateCreated: Timestamp? = null
)