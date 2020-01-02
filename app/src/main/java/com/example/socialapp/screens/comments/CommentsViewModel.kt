package com.example.socialapp.screens.comments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.common.Result
import com.example.socialapp.model.Comment
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import timber.log.Timber

class CommentsViewModel(private val postId: String) : ViewModel() {

    private val repo = FirestoreRepository()

    init {
        Timber.i("init called")
    }

    // Two way databinding variable that holds comment input message
    val newPostContent = MutableLiveData<String>("")

    fun addNewComment() = viewModelScope.launch {
        if (newPostContent.value!!.isNotBlank()) {
            val addTask = repo.uploadNewComment(postId, newPostContent.value!!)
            when (addTask) {
                is Result.Value -> {
                    newPostContent.value = ""
                }
                is Result.Error -> {
                    // TODO: trigger event with error message <String>
                }
            }
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
