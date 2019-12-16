package com.example.socialapp.screens.adverts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.model.Advertisement
import com.example.socialapp.model.Filters
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class AdvertsViewModel : ViewModel() {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val repo = FirestoreRepository()

    val filters = MutableLiveData<Filters>(Filters())

    init {
        Timber.i("init called")
    }

    fun createNewAdvert(advert: Advertisement): Task<Void> {
        return repo.addNewAdvertisement(advert)
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        viewModelJob.cancel()
        super.onCleared()
    }
}