package com.example.socialapp.screens.home

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.example.socialapp.model.Post
import com.example.socialapp.repository.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class TimelinePostsDataSource(private val scope: CoroutineScope, private val userUid: String) :
    ItemKeyedDataSource<String, Post>(), KoinComponent {

    private val repo: FirestoreRepository by inject()

    class Factory(private val scope: CoroutineScope, private val userUid: String) :
        DataSource.Factory<String, Post>() {
        override fun create(): DataSource<String, Post> = TimelinePostsDataSource(scope, userUid)
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Post>
    ) {
        scope.launch {
            val items = repo.getUserTimeline(userUid, params.requestedLoadSize)
            callback.onResult(items)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Post>) {
        scope.launch {
            val items =
                repo.getUserTimeline(userUid, params.requestedLoadSize, loadAfter = params.key)
            callback.onResult(items)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Post>) {
        scope.launch {
            val items =
                repo.getUserTimeline(userUid, params.requestedLoadSize, loadBefore = params.key)
            callback.onResult(items)
        }
    }

    override fun getKey(item: Post): String = item.postId

}