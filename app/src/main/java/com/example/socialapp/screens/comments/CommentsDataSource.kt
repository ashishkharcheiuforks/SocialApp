package com.example.socialapp.screens.comments

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.model.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CommentsDataSource(private val scope: CoroutineScope, private val postId: String) :
    ItemKeyedDataSource<String, Comment>() {

    class Factory(private val scope: CoroutineScope, private val postId: String) :
        DataSource.Factory<String, Comment>() {
        override fun create(): DataSource<String, Comment> =
            CommentsDataSource(scope, postId)
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Comment>
    ) {
        scope.launch {
            val items = FirestoreRepository()
                .getPostComments(postId, params.requestedLoadSize)
            callback.onResult(items)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Comment>) {
        scope.launch {
            val items = FirestoreRepository().getPostComments(
                postId,
                params.requestedLoadSize,
                loadAfter = params.key
            )
            callback.onResult(items)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Comment>) {
        scope.launch {
            val items = FirestoreRepository().getPostComments(
                postId,
                params.requestedLoadSize,
                loadBefore = params.key
            )
            callback.onResult(items)
        }
    }

    override fun getKey(item: Comment): String = item.commentId

}