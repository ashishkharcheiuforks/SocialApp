package com.example.socialapp.screens.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.socialapp.model.Post
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.common.Result
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

class UserProfileViewModel(private val uid: String) : ViewModel() {

    private val repo = FirestoreRepository()

    init {
        Timber.i("init called")
    }

    val user =
        repo.getUserLiveData(uid)

    val friendshipStatus = repo.getFriendshipStatus(uid)


    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPrefetchDistance(10)
        .setPageSize(20)
        .build()

    private val dataSource = UserPostsDataSource.Factory(viewModelScope, uid)

    val posts: LiveData<PagedList<Post>> =
        LivePagedListBuilder<String, Post>(
            dataSource,
            config
        ).build()

    fun refreshPosts() = posts.value?.dataSource?.invalidate()

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }

    fun likePost(postId: String) = viewModelScope.launch{
        val likeResult = repo.likePost(postId)
        when(likeResult){
            is Result.Error -> {  }
            is Result.Value -> { }
        }
    }

    fun unlikePost(postId: String) = viewModelScope.launch{
        val unlikeResult = repo.unlikePost(postId)
        when(unlikeResult){
            is Result.Error -> { }
            is Result.Value -> { }
        }
    }

    fun inviteToFriends() = viewModelScope.launch{
        val inviteTask = repo.inviteToFriends(uid)
        when(inviteTask){
            is Result.Error -> { }
            is Result.Value -> { }
        }
    }

    fun acceptFriendRequest() = viewModelScope.launch {
        val acceptTask = repo.acceptFriendRequest(uid)
        when(acceptTask){
            is Result.Error -> { }
            is Result.Value -> { }
        }
    }

    fun cancelFriendRequest() = viewModelScope.launch {
        val deleteTask = repo.deleteFriendRequest(uid)
        when(deleteTask){
            is Result.Error -> { }
            is Result.Value -> { }
        }
    }

}
