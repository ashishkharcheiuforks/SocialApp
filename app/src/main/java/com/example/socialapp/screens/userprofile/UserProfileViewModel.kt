package com.example.socialapp.screens.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.socialapp.livedata.DocumentSnapshotLiveData
import com.example.socialapp.livedata.UserLiveData
import com.example.socialapp.model.Post
import com.example.socialapp.FirestoreRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class UserProfileViewModel(private val uid: String) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    private val dataSource = UserPostsDataSource.Factory(uiScope, uid)

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

    fun likeThePost(postId: String): Task<Void> {
        return repo.likeThePost(postId)
    }

    fun unlikeThePost(postId: String): Task<Void>{
        return repo.unlikeThePost(postId)
    }

    fun inviteToFriends(): Task<Void> {
        return repo.inviteToFriends(uid)
    }

    fun acceptFriendRequest(): Task<Void> {
        return repo.acceptFriendRequest(uid)
    }

    fun cancelFriendRequest(): Task<Void> {
        return repo.deleteFriendRequest(uid)
    }

}
