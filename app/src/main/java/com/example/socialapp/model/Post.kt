package com.example.socialapp.model

import com.google.firebase.Timestamp
import io.reactivex.Observable

data class Post(
    val postId: String = "",
    val postImage: String? = "",
    val content: String = "",
    val dateCreated: Timestamp? = null,
    val postLikesNumber: Int = 0,
    val postCommentsNumber: Int = 0,
    val user: User? = null,
    val postLiked: Observable<Boolean>
)