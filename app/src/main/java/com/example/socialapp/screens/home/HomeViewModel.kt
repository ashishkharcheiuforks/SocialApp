package com.example.socialapp.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.socialapp.repository.FirestoreRepository
import com.example.socialapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import com.example.socialapp.common.Result

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

    fun likePost(postId: String) = viewModelScope.launch{
        val likeResult = repo.likePost(postId)

        when(likeResult){
            is Result.Error -> { Timber.d("like post result error") }
            is Result.Value -> { Timber.d("like post result value") }
        }
    }

    fun unlikePost(postId: String) = viewModelScope.launch{
        val unlikeResult = repo.unlikePost(postId)

        when(unlikeResult){
            is Result.Error -> { Timber.d("unlike post result error") }
            is Result.Value -> { Timber.d("unlike post result value") }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("onCleared() called")
    }
}