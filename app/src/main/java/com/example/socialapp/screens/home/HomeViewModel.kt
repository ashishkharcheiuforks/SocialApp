package com.example.socialapp.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class HomeViewModel : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val auth = FirebaseAuth.getInstance()
    private val repo = FirestoreRepository()

    init {
        Timber.i("Init called")
    }

    // Configuration on loading paged list of posts
    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPrefetchDistance(10)
        .setPageSize(20)
        .build()

    // Data source for loading paged posts
    private val dataSource =
        TimelinePostsDataSource.Factory(uiScope, auth.uid!!)

    // PagedList of posts as LiveData
    val posts: LiveData<PagedList<Post>> =
        LivePagedListBuilder<String, Post>(dataSource, config).build()

    // Reloads the posts
    fun refreshPosts() {
        posts.value?.dataSource?.invalidate()
    }

    fun likePost(postId: String): Task<Void> {
        return repo.likePost(postId)
    }

    fun unlikePost(postId: String): Task<Void> {
        return repo.unlikePost(postId)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("onCleared() called")
    }
}