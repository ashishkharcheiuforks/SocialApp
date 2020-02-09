package com.example.socialapp.screens.chat

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.example.socialapp.model.LastMessage
import com.example.socialapp.repository.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ChatRoomsDataSource(private val scope: CoroutineScope) :
    ItemKeyedDataSource<String, LastMessage>(), KoinComponent {

    val repo: FirestoreRepository by inject()

    class Factory(private val scope: CoroutineScope) :
        DataSource.Factory<String, LastMessage>() {
        override fun create(): DataSource<String, LastMessage> =
            ChatRoomsDataSource(scope)
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<LastMessage>
    ) {
        scope.launch {
            val items = repo
                .getChatRooms(params.requestedLoadSize)
            callback.onResult(items)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<LastMessage>) {
        scope.launch {
            val items = repo.getChatRooms(
                params.requestedLoadSize,
                loadAfter = params.key
            )
            callback.onResult(items)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<LastMessage>) {
        scope.launch {
            val items = repo.getChatRooms(
                params.requestedLoadSize,
                loadBefore = params.key
            )
            callback.onResult(items)
        }
    }

    override fun getKey(item: LastMessage): String = item.chatRoomId

}