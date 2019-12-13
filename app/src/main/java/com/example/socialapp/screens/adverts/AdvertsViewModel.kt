package com.example.socialapp.screens.main.adverts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class AdvertsViewModel: ViewModel() {

    val text = MutableLiveData<String>()

    init {
        Timber.i("init called")
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared() called")
    }
}