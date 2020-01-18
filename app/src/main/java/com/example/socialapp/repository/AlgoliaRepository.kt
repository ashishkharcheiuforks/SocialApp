package com.example.socialapp.repository

import com.algolia.search.saas.Client
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

/*
* This layer is only for project purpose
* In real life case all the integration with Algolia 3rd party service
* should be done from the cloud functions side
* */

class AlgoliaRepository {

    private val apiKey = "02097ce2016d6a6f130949f04093678d"
    private val applicationID = "78M3CITBN7"

    private val client = Client(applicationID, apiKey)
    private val index = client.getIndex("users")

    private val auth = FirebaseAuth.getInstance()

    fun updateProfilePictureUrl(url: String) {

        val jsonObject =
            JSONObject().put("profile_picture_url", url)

        index.partialUpdateObjectAsync(jsonObject, auth.uid!!, null)
    }

    fun updateNameAndNickname(firstName: String, nickname: String) {
        val jsonObject = JSONObject()
            .put("first_name", firstName)
            .put("nickname", nickname)

        index.partialUpdateObjectAsync(jsonObject, FirebaseAuth.getInstance().uid.toString(), null)
    }

    fun insertUser(firstName: String, nickname: String, profilePictureUrl: String){
        val jsonObject =
            JSONObject()
                .put("first_name", firstName)
                .put("nickname", nickname)
                .put("profile_picture_url", profilePictureUrl)

        val client = Client(applicationID, apiKey)
        val index = client.getIndex("users")

        index.addObjectAsync(
            jsonObject,
            auth.currentUser?.uid.toString(),
            null
        )
    }


//    fun searchForUser(phrase: String): List<User>{
//        val array = MutableLiveData<MutableList<User>>(mutableListOf())
//        val query: Query = Query(phrase)
//            .setAttributesToRetrieve("first_name", "nickname", "profile_picture_url")
//            .setHitsPerPage(30)
//
//
//        index.searchAsync(query) { content, algoliaException ->
//            try {
//                val hits: JSONArray = content!!.getJSONArray("hits")
//                for (i in 0 until hits.length()) {
//                    val jsonObject = hits.getJSONObject(i)
//                    Timber.d(content.toString())
//
//                    array.value!!.add(
//                        User(
//                            uid = jsonObject.getString("objectID"),
//                            firstName = jsonObject.getString("first_name"),
//                            nickname = jsonObject.getString("nickname"),
//                            dateOfBirth = null,
//                            profilePictureUri = Uri.parse(jsonObject.getString("profile_picture_url")),
//                            aboutMe = null
//                        )
//                    )
//
//                }
//
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//        }
//        return array.value!!.toList()
//    }
}