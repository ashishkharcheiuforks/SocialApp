package com.example.socialapp.model

import com.google.firebase.Timestamp
import io.reactivex.Observable

data class Post(
    val postId: String,
    val postImage: String?,
    val postContent: String,
    val postDateCreated: Timestamp,
    val postLikesNumber: Int,
    val postCommentsNumber: Int,
    val user: User,
    val postLiked: Observable<Boolean>
)