package com.example.socialapp.screens.searchuser

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.algolia.search.saas.Client
import com.algolia.search.saas.Query
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber


class SearchUserViewmodel : ViewModel() {

    private val apiKey = "0f0019215a9b1c0d31611e2004ba61cf"
    private val applicationID = "78M3CITBN7"

    private val _users = MutableLiveData<MutableList<SearchUserItem>>()
    val users: LiveData<MutableList<SearchUserItem>>
        get() = _users

    init {
        Timber.i("Init called")
        _users.value = mutableListOf()
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        super.onCleared()
    }

    fun searchForUser(string: String) {
        _users.value!!.clear()
        val query: Query = Query(string)
            .setAttributesToRetrieve("first_name", "nickname", "profile_picture_url")
            .setHitsPerPage(30)

        val client = Client(applicationID, apiKey)
        val index = client.getIndex("users")

        index.searchAsync(query) { content, algoliaException ->
            try {
                val hits: JSONArray = content!!.getJSONArray("hits")
                val array: MutableList<SearchUserItem> = mutableListOf()
                for (i in 0 until hits.length()) {
                    val jsonObject = hits.getJSONObject(i)
                    Timber.d(content.toString())

                    array.add(
                        SearchUserItem(
                            name = jsonObject.getString("first_name"),
                            nickname = jsonObject.getString("nickname"),
                            profilePic = Uri.parse(jsonObject.getString("profile_picture_url")),
                            uid = jsonObject.getString("objectID")
                        )
                    )

                }
                _users.value = array
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}