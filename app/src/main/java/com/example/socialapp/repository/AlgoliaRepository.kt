package com.example.socialapp.repository

import com.algolia.search.saas.Client
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

/**
 * In real life case all write operations to the Algolia service
 * would be triggered by cloud functions after right trigger
 * */

class AlgoliaRepository {

    private val apiKey = "02097ce2016d6a6f130949f04093678d"
    private val applicationID = "78M3CITBN7"

    private val client = Client(applicationID, apiKey)
    private val index = client.getIndex("users")

    private val auth = FirebaseAuth.getInstance()

    fun updateProfilePictureUrl(url: String) {
        val jsonObject = JSONObject().put("profile_picture_url", url)

        index.partialUpdateObjectAsync(jsonObject, auth.uid!!, null)
    }

    fun updateNameAndNickname(firstName: String?, nickname: String?) {
        val jsonObject = JSONObject().apply {
            firstName?.let { put("first_name", it) }
            nickname?.let { put("nickname", it) }
        }
        if (jsonObject.length() > 0) {
            index.partialUpdateObjectAsync(
                jsonObject,
                auth.uid!!,
                null
            )
        } else return
    }

    fun insertUser(firstName: String?, nickname: String?, profilePictureUrl: String?) {
        val jsonObject = JSONObject().apply {
            firstName?.let { put("first_name", it) }
            nickname?.let { put("nickname", it) }
            profilePictureUrl?.let { put("profile_picture_url", it) }
        }
        if (jsonObject.length() > 0) {
            index.addObjectAsync(
                jsonObject,
                auth.uid!!,
                null
            )
        } else return
    }

}