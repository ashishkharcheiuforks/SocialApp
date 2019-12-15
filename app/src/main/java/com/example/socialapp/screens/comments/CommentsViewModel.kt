package com.example.socialapp.screens.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.model.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class CommentsViewModel(private val postId: String) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val newPostContent = MutableLiveData<String>("")

    init {
        Timber.i("init called")
    }

    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPrefetchDistance(10)
        .setPageSize(20)
        .build()

    private val dataSource = CommentsDataSource.Factory(uiScope, postId)

    val comments: LiveData<PagedList<Comment>> =
        LivePagedListBuilder<String, Comment>(
            dataSource,
            config
        ).build()


    fun addNewComment(){
        // TODO(): Add proper validation for the add comment button
        if(newPostContent.value!!.trim().isNotEmpty() ){
           FirestoreRepository().uploadNewComment(postId, newPostContent.value!!)
        }
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}
