package com.example.socialapp.screens.adverts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
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

    val filters = MutableLiveData<Filters>()

    init {
        Timber.i("init called")
        filters.value = Filters()
    }

    // Configuration on loading paged list of posts
    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPrefetchDistance(10)
        .setPageSize(20)
        .build()

    //Maybe let filters variable be observable with rx?

    // Data source for loading paged posts
    private var dataSource =
        AdvertsDataSource.Factory(uiScope, filters.value!!)

    // mutableLiveData holding LiveData?
    // list of datasources and list of adverts and

    // PagedList of advertisements as LiveData, Observed in fragment
    var adverts: LiveData<PagedList<Advertisement>> =
        LivePagedListBuilder<String, Advertisement>(
            dataSource,
            config
        ).build()


    // Reloads the adverts
    fun refreshAdverts() {
        Timber.d("refreshAdverts called")
        adverts.value?.dataSource?.invalidate()
    }

    fun createNewAdvert(advert: Advertisement): Task<Void> {
        return FirestoreRepository().addNewAdvertisement(advert)
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        viewModelJob.cancel()
        super.onCleared()
    }
}