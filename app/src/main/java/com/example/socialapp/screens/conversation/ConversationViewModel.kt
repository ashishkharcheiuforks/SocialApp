package com.example.socialapp.screens.conversation

import androidx.lifecycle.*
import com.example.socialapp.repository.FirestoreRepository
import com.example.socialapp.common.Result
import com.example.socialapp.model.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class ConversationViewModel(private val otherUserId: String) : ViewModel() {

    val repo = FirestoreRepository()

    // Two-way data binding variables
    val message = MutableLiveData<String>("")

    private val chatRoomId = MutableLiveData<String>()

    @ExperimentalCoroutinesApi
    private val _messagesList = MutableLiveData<List<Message>>()
    val messagesList: LiveData<List<Message>>
        get() = _messagesList

    init {
        viewModelScope.launch {
            val chatId = repo.getChatRoomId(otherUserId)
            chatRoomId.value = chatId
            repo.chatMessagesFlow(chatId).collect {
                _messagesList.value = it
            }
        }

    }

    val user = liveData {
        val user = repo.getUser(otherUserId)
        emit(user)
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }

    fun sendMessage() {
        if (message.value.isNullOrEmpty() || chatRoomId.value.isNullOrEmpty()) return
        viewModelScope.launch {
            val sendTask = repo.sendTextMessage(chatRoomId.value!!, message.value!!)
            when (sendTask) {
                is Result.Value -> {
                    message.value = ""
                }
                is Result.Error -> {

                }
            }
        }
    }


}
