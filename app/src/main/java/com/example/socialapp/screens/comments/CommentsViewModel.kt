package com.example.socialapp.screens.comments

import androidx.lifecycle.*
import com.example.socialapp.repository.FirestoreRepository
import com.example.socialapp.common.Result
import com.example.socialapp.model.Comment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class CommentsViewModel(private val postId: String) : ViewModel() {

    private val repo = FirestoreRepository()

    init {
        Timber.i("init called")
    }

    @ExperimentalCoroutinesApi
    val comments: LiveData<List<Comment>> = liveData {
        repo.commentsFlow(postId).collect {
            emit(it)
        }
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

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}
