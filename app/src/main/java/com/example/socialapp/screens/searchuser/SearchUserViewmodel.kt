package com.example.socialapp.screens.searchuser

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.algolia.search.saas.Client
import com.algolia.search.saas.Query
import com.example.socialapp.AlgoliaRepository
import com.example.socialapp.model.User
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber


class SearchUserViewmodel : ViewModel() {

    private val apiKey = "0f0019215a9b1c0d31611e2004ba61cf"
    private val applicationID = "78M3CITBN7"

    val searchPhrase = MediatorLiveData<String>()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

    init {
        Timber.i("Init called")
        _users.value = mutableListOf()
        searchPhrase.addSource(searchPhrase) { searchForUser(it) }
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        searchPhrase.removeSource(searchPhrase)
        super.onCleared()
    }

    fun searchForUser(string: String) {
//        if (string.length > 3) {
//            _users.value = AlgoliaRepository().searchForUser(string)
//        }
                val query: Query = Query(string)
            .setAttributesToRetrieve("first_name", "nickname", "profile_picture_url")
            .setHitsPerPage(30)

        val client = Client(applicationID, apiKey)
        val index = client.getIndex("users")

        index.searchAsync(query) { content, _ ->
            try {
                val hits: JSONArray = content!!.getJSONArray("hits")
                val array: MutableList<User> = mutableListOf()
                for (i in 0 until hits.length()) {
                    val jsonObject = hits.getJSONObject(i)
                    Timber.d(content.toString())

                    array.add(
                        User(
                            uid = jsonObject.getString("objectID"),
                            firstName = jsonObject.getString("first_name"),
                            nickname = jsonObject.getString("nickname"),
                            dateOfBirth = null,
                            profilePictureUri = Uri.parse(jsonObject.getString("profile_picture_url")),
                            aboutMe = null
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