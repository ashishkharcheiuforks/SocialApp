package com.example.socialapp.screens.adverts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.common.Result
import com.example.socialapp.model.Advertisement
import com.example.socialapp.model.Filters
import com.example.socialapp.repository.FirestoreRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class AdvertsViewModel(val repo: FirestoreRepository) : ViewModel() {

    val filters = MutableLiveData<Filters>(Filters())

    init {
        Timber.i("init called")
    }

    fun createNewAdvert(advert: Advertisement) = viewModelScope.launch {
        val createTask = repo.addNewAdvertisement(advert)
        when(createTask){
            is Result.Value -> {}
            is Result.Error -> {}
        }
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }
}