package com.example.socialapp.screens.invites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.common.Result
import com.example.socialapp.livedata.QuerySnapshotLiveData
import com.example.socialapp.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber


class InvitesViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    init {
        Timber.i("init called")
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }

    fun acceptFriendRequest(uid: String) = viewModelScope.launch {
        val acceptTask = repo.acceptFriendRequest(uid)
        when (acceptTask) {
            is Result.Value -> {
            }
            is Result.Error -> {
            }
        }
    }

    fun deleteFriendRequest(uid: String) = viewModelScope.launch {
        val deleteTask = repo.deleteFriendRequest(uid)
        when (deleteTask) {
            is Result.Value -> {
            }
            is Result.Error -> {
            }
        }
    }

    @ExperimentalCoroutinesApi
    val invites: LiveData<List<User>> = liveData {
        repo.invitesFlow().collect {
            val list = it.map{uid ->
                repo.getUser(uid)
            }
            emit(list)
        }
    }


}