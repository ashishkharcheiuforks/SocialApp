package com.example.socialapp.model

import android.net.Uri

data class PostItem(
    val postId: String,
    val postImage: Uri?,
    val postContent: String,
    val postDateCreated: String,
    val postLikesNumber: Int,
    val postCommentsNumber: Int
)