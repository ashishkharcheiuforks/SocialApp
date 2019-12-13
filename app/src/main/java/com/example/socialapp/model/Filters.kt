package com.example.socialapp.model

data class Filter(
    val game: String? = null,
    val communicationLanguage: String? = null,
    val minAge: Long? = null,
    val maxAge: Long? = null
)