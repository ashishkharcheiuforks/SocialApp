package com.example.socialapp.screens.friends

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.example.socialapp.model.User
import com.example.socialapp.repository.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class FriendsDataSource(private val scope: CoroutineScope, private val userUid: String) :
    ItemKeyedDataSource<String, User>(), KoinComponent {

    val repo: FirestoreRepository by inject()

    class Factory(private val scope: CoroutineScope, private val userUid: String) :
        DataSource.Factory<String, User>() {
        override fun create(): DataSource<String, User> =
            FriendsDataSource(scope, userUid)
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<User>
    ) {
        scope.launch {
            val items = repo
                .getFriends(userUid, params.requestedLoadSize)
            callback.onResult(items)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<User>) {
        scope.launch {
            val items = repo.getFriends(
                userUid,
                params.requestedLoadSize,
                loadAfter = params.key
            )
            callback.onResult(items)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<User>) {
        scope.launch {
            val items = repo.getFriends(
                userUid,
                params.requestedLoadSize,
                loadBefore = params.key
            )
            callback.onResult(items)
        }
    }

    override fun getKey(user: User): String = user.uid

}