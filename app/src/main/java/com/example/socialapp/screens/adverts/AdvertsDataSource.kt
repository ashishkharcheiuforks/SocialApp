package com.example.socialapp.screens.adverts

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.example.socialapp.FirestoreRepository
import com.example.socialapp.model.Advertisement
import com.example.socialapp.model.Filters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AdvertsDataSource(private val scope: CoroutineScope, private val filters: Filters) :
    ItemKeyedDataSource<String, Advertisement>() {

    class Factory(private val scope: CoroutineScope, private val filters: Filters) :
        DataSource.Factory<String, Advertisement>() {
        override fun create(): DataSource<String, Advertisement> =
            AdvertsDataSource(scope, filters)
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Advertisement>
    ) {
        scope.launch {
            val items = FirestoreRepository()
                .getAdvertisements(filters, params.requestedLoadSize)
            callback.onResult(items)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Advertisement>) {
        scope.launch {
            val items = FirestoreRepository().getAdvertisements(
                filters,
                params.requestedLoadSize,
                loadAfter = params.key
            )
            callback.onResult(items)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Advertisement>) {
        scope.launch {
            val items = FirestoreRepository().getAdvertisements(
                filters,
                params.requestedLoadSize,
                loadBefore = params.key
            )
            callback.onResult(items)
        }
    }

    override fun getKey(item: Advertisement): String = item.advertisementId!!

}