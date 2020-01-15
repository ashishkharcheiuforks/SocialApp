package com.example.socialapp.screens.newchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.socialapp.model.User
import com.example.socialapp.screens.friends.FriendsDataSource
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class NewChatViewModel : ViewModel() {

    val auth = FirebaseAuth.getInstance()

    // Data source for loading paged posts
    private val dataSource = FriendsDataSource.Factory(viewModelScope, auth.uid!!)

    // Configuration on loading paged list of posts
    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPrefetchDistance(10)
        .setPageSize(20)
        .build()

    // PagedList of posts as LiveData
    val friends: LiveData<PagedList<User>> =
        LivePagedListBuilder<String, User>(
            dataSource,
            config
        ).build()

    // Reloads the friends list
    fun refreshFriends() {
        friends.value?.dataSource?.invalidate()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared() called")
    }
}
