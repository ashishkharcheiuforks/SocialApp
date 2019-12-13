package com.example.socialapp.screens.userprofile

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.example.socialapp.model.Post
import com.example.socialapp.screens.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UserPostsDataSource(private val scope: CoroutineScope, private val userUid: String) :
    ItemKeyedDataSource<String, Post>() {

    class Factory(private val scope: CoroutineScope, private val userUid: String) :
        DataSource.Factory<String, Post>() {
        override fun create(): DataSource<String, Post> =
            UserPostsDataSource(scope, userUid)
    }


    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Post>
    ) {
        scope.launch {
            val items = FirestoreRepository().getUserPosts(userUid, params.requestedLoadSize)
            callback.onResult(items)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Post>) {
        scope.launch {
            val items = FirestoreRepository().getUserPosts(
                userUid,
                params.requestedLoadSize,
                loadAfter = params.key
            )
            callback.onResult(items)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Post>) {
        scope.launch {
            val items = FirestoreRepository().getUserPosts(
                userUid,
                params.requestedLoadSize,
                loadBefore = params.key
            )
            callback.onResult(items)
        }
    }

    override fun getKey(item: Post): String = item.postId

}