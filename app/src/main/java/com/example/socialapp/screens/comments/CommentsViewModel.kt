package com.example.socialapp.screens.comments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.model.Comment
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class CommentsViewModel(private val postId: String) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val repo = FirestoreRepository()

    init {
        Timber.i("init called")
    }

    val newPostContent = MutableLiveData<String>("")

    fun addNewComment() {
        if (newPostContent.value!!.isNotBlank()) {
            repo.uploadNewComment(postId, newPostContent.value!!)
            newPostContent.value = ""
        }
    }

    fun addCommentsListener(onComplete: (List<Comment>) -> Unit): ListenerRegistration {
        return repo.addCommentsListener(postId, onComplete)
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}
