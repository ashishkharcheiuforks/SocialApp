package com.example.socialapp.screens.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.socialapp.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class FriendsViewModel : ViewModel() {

    init {
        Timber.i("init called")
    }

    private val auth = FirebaseAuth.getInstance()

    // Configuration on loading paged list of posts
    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPrefetchDistance(10)
        .setPageSize(20)
        .build()

    // Data source for loading paged posts
    private val dataSource = FriendsDataSource.Factory(viewModelScope, auth.uid!!)

    // PagedList of posts as LiveData
    val friends: LiveData<PagedList<User>> =
        LivePagedListBuilder<String, User>(
            dataSource,
            config
        ).build()

    // Reloads the posts
    fun refreshFriends() {
        friends.value?.dataSource?.invalidate()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared() called")
    }
}