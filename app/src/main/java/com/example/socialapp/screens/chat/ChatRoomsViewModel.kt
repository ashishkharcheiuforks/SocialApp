package com.example.socialapp.screens.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.socialapp.model.LastMessage

class ChatRoomsViewModel : ViewModel() {

    // Configuration on loading paged list of posts
    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPrefetchDistance(10)
        .setPageSize(20)
        .build()

    // Data source for loading paged posts
    private val dataSource = ChatRoomsDataSource.Factory(viewModelScope)

    // PagedList of posts as LiveData
    val chatRooms: LiveData<PagedList<LastMessage>> =
        LivePagedListBuilder<String, LastMessage>(
            dataSource,
            config
        ).build()

    // Reloads the posts
    fun refreshChatRooms() {
        chatRooms.value?.dataSource?.invalidate()
    }

}
