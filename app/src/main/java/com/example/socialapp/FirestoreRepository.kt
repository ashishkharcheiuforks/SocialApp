package com.example.socialapp

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.socialapp.livedata.DocumentSnapshotLiveData
import com.example.socialapp.livedata.QuerySnapshotLiveData
import com.example.socialapp.livedata.UserLiveData
import com.example.socialapp.model.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber


class FirestoreRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private val algolia = AlgoliaRepository()


    fun insertUserDataOnRegistration(
        firstName: String,
        nickname: String,
        dateOfBirth: Timestamp
    ): Task<Void> {
        val defaultProfilePictureUrl =
            "https://firebasestorage.googleapis.com/v0/b/social-app-a3759.appspot.com/o/profilepic.jpg?alt=media&token=ad32501b-383e-4a25-b1d2-b3586ee338bd"
        val userProfileInfo = hashMapOf(
            "firstName" to firstName,
            "nickname" to nickname,
            "dateOfBirth" to dateOfBirth,
            "profilePictureUrl" to defaultProfilePictureUrl
        )
        val userDocRef = db.document("users/${auth.uid}")
        return userDocRef.set(userProfileInfo)
    }

    fun getUserLiveData(userId: String): LiveData<User> {
        val userDocRef = db.document("users/${userId}")
        return UserLiveData(userDocRef)
    }

    // Updates reference link to the user profile picture in firestore database
    fun updateProfilePictureUrl(url: String) {
        val userDocRef = db.document("users/${auth.uid}")
        val data = hashMapOf<String, Any>("profilePictureUrl" to url)
        userDocRef.update(data)
    }

    // Updates user profile with provided non-null values
    suspend fun updateUserProfileInfo(
        firstName: String?,
        nickname: String?,
        dateOfBirth: Timestamp?,
        profilePictureUri: Uri?
    ): Task<Void> {
        val userDocRef = db.document("users/${auth.uid}")

        // Data set with fields to update in user document
        val data = mutableMapOf<String, Any>()

        // If value exists add it to update data set
        firstName?.let { data.put("firstName", it) }
        nickname?.let { data.put("nickname", it) }
        dateOfBirth?.let { data.put("dateOfBirth", it) }

        profilePictureUri?.let {
            changeUserProfilePicture(profilePictureUri)
        }

        algolia.updateNameAndNickname(firstName!!, nickname!!)

        return userDocRef.update(data)
    }

    /**    Sending friend request to the user    **/

    fun inviteToFriends(uid: String): Task<Void> {
        val data = mapOf<String, Any>("status" to FriendshipStatus.INVITATION_SENT.status)

        val myFriends = db.collection("users")
            .document(auth.uid!!)
            .collection("friends")
            .document(uid)

        return myFriends.set(data)
    }

    /**    Accepting friend request    **/

    fun acceptFriendRequest(uid: String): Task<Void> {
        val data = mapOf<String, Any>("status" to FriendshipStatus.ACCEPTED.status)
        val friendDocRef = db.document("users/${auth.uid}/friends/$uid")
        return friendDocRef.update(data)
    }

    /**    Cancel pending friend request    **/

    fun deleteFriendRequest(uid: String): Task<Void> {
        val friendDocRef = db.document("users/${auth.uid}/friends/$uid")
        return friendDocRef.delete()
    }

    /**    Snapshot for real-time friendship status    **/

    fun getFriendshipStatus(uid: String): DocumentSnapshotLiveData {
        val friendDocRef = db.document("users/${auth.uid}/friends/$uid")
        return DocumentSnapshotLiveData(friendDocRef)
    }

    fun fetchInvitesLiveData(): QuerySnapshotLiveData {
        val friendsCollectionRef = db.collection("users/${auth.uid}/friends")
        val status = FriendshipStatus.INVITATION_RECEIVED.status
        val query = friendsCollectionRef.whereEqualTo("status", status)
        return QuerySnapshotLiveData(query)
    }

    /**     NEW STUFF   **/

    /**
     * Adds a new post to 'savedPosts' collection
     * and cloud functions populates reference to it
     * in user friends timelines
     * */

    fun addPost(postContent: String?, postImage: Uri?) {
        val newPostDocRef = db.collection("posts").document()
        val newPostId = newPostDocRef.id

        // New post document fields
        val data = HashMap<String, Any>()

        data["createdByUserUid"] = auth.uid!!
        // Server Timestamp for date and time of post creation
        data["dateCreated"] = FieldValue.serverTimestamp()
        // Initial value for number of likes
        data["likesNumber"] = 0
        // Initial value for number of comments
        data["commentsNumber"] = 0

        // If post text content exists add it to the document
        postContent?.let { data.put("postContent", it) }

        // If image was passed to add to the document
        // it will be uploaded
        postImage?.let {
            //TODO(DEV): create random name for the uploaded file
            val postImagesReference = storageReference.child("posts/$newPostId/images/image.jpeg")

            uploadPictureAndReturnUrl(it, postImagesReference).addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Timber.d("Uploaded and returned new picture url: ${task.result}")
                updatePostImageUrl(newPostId, task.result!!)
            }
        }

        newPostDocRef.set(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Added new post successfully")
                } else
                    Timber.d(task.exception, "Failed to add the post")
            }
    }


    // Uploads Picture to the provided path in Firebase Storage and returns Task
    // with newly uploaded picture Url as a result
    fun uploadPictureAndReturnUrl(pictureToUpload: Uri, path: StorageReference): Task<Uri> {
        val uploadTask = path.putFile(pictureToUpload)

        return uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            return@Continuation path.downloadUrl
        })

    }

    fun updatePostImageUrl(postId: String, imageUrl: Uri) {
        val postDocRef = db.document("posts/$postId")
        postDocRef.update(mapOf("postImage" to imageUrl))
    }

    suspend fun getUserTimeline(
        userUid: String,
        pageSize: Int,
        loadBefore: String? = null,
        loadAfter: String? = null
    ): List<Post> {
        val timelineCollRef = db.collection("users/$userUid/timeline")
        var query = timelineCollRef
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        loadBefore?.let {
            val item = timelineCollRef.document(it).get().await()
            query = query.endBefore(item)
        }

        loadAfter?.let {
            val item = timelineCollRef.document(it).get().await()
            query = query.startAfter(item)
        }

        return query.get().await().map {
            getPost(it.id, it.getString("createdByUserUid").toString())
        }
    }

    suspend fun getFriends(
        userUid: String,
        pageSize: Int,
        loadBefore: String? = null,
        loadAfter: String? = null
    ): List<User> {
        val friendsCollectionRef = db.collection("users").document(userUid).collection("friends")
        var query = friendsCollectionRef
            .whereEqualTo("status", FriendshipStatus.ACCEPTED.status)
            .limit(pageSize.toLong())

        loadBefore?.let {
            val item = friendsCollectionRef.document(it).get().await()
            query = query.endBefore(item)
        }

        loadAfter?.let {
            val item = friendsCollectionRef.document(it).get().await()
            query = query.startAfter(item)
        }

        return query.get().await().map {
            getUser(it.id)
        }
    }

    suspend fun getUserPosts(
        userUid: String,
        pageSize: Int,
        loadBefore: String? = null,
        loadAfter: String? = null
    ): List<Post> {
        val user = getUser(userUid)
        val postsCollRef = db.collection("posts")

        var query = postsCollRef
            .whereEqualTo("createdByUserUid", userUid)
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        loadBefore?.let {
            val item = postsCollRef.document(it).get().await()
            query = query.endBefore(item)
        }

        loadAfter?.let {
            val item = postsCollRef.document(it).get().await()
            query = query.startAfter(item)
        }

        return query.get().await().map {
            Post(
                postId = it.id,
                postLikesNumber = it.getLong("likesNumber")!!.toInt(),
                postImage = it.getString("postImage"),
                postDateCreated = it.getTimestamp("dateCreated")!!,
                postContent = it.get("postContent").toString(),
                postCommentsNumber = it.getLong("commentsNumber")!!.toInt(),
                user = user,
                postLiked = getPostLikeStatus(it.id)
            )
        }
    }

    suspend fun getPostComments(
        postId: String,
        pageSize: Int,
        loadBefore: String? = null,
        loadAfter: String? = null
    ): List<Comment> {
        val commentsCollRef = db.collection("posts/${postId}/comments")
        var query = commentsCollRef
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        loadBefore?.let {
            val item = commentsCollRef.document(it).get().await()
            query = query.endBefore(item)
        }

        loadAfter?.let {
            val item = commentsCollRef.document(it).get().await()
            query = query.startAfter(item)
        }

        return query.get().await().map {
            Comment(
                it.id,
                it.getString("content")!!,
                it.getTimestamp("dateCreated")!!,
                getUser(it.getString("createdByUserId")!!)
            )
        }
    }

    suspend fun getAdvertisements(
        filters: Filters,
        pageSize: Int,
        loadBefore: String? = null,
        loadAfter: String? = null
    ): List<Advertisement> {
        Timber.d(filters.toString())
        val advertsCollRef = db.collection("advertisements")
        var query = advertsCollRef
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())
            .also {
                if (filters.game != null) {
                    Timber.d("game filter inside repo set")
                    it.whereEqualTo("game", filters.game)
                }
                if (filters.communicationLanguage != null) {
                    Timber.d("language filter inside repo set")
                    it.whereEqualTo("communicationLanguage", filters.communicationLanguage)
                }
                if (filters.playersNumber != null) {
                    Timber.d("players number filter inside repo set")
                    it.whereEqualTo("playersNumber", filters.playersNumber)
                }
            }

        loadBefore?.let {
            val item = advertsCollRef.document(it).get().await()
            query = query.endBefore(item)
        }

        loadAfter?.let {
            val item = advertsCollRef.document(it).get().await()
            query = query.startAfter(item)
        }

        return query.get().await().map {
            Advertisement(
                advertisementId = it.id,
                filters = Filters(
                    game = it.getString("game"),
                    communicationLanguage = it.getString("communicationLanguage"),
                    playersNumber = it.getLong("playersNumber")
                ),
                description = it.getString("description"),
                dateCreated = it.getTimestamp("dateCreated")!!,
                user = getUser(it.getString("createdByUserUid")!!),
                createdByUserUid = it.getString("createdByUserUid")
            )
        }

    }

    suspend fun getPost(postId: String, userId: String): Post {
        val user = getUser(userId)
        val postDocumentSnapshot = db.document("posts/$postId").get().await()

        return Post(
            postId = postId,
            postCommentsNumber = postDocumentSnapshot.getLong("commentsNumber")!!.toInt(),
            postContent = postDocumentSnapshot.getString("postContent").toString(),
            postDateCreated = postDocumentSnapshot.getTimestamp("dateCreated")!!,
            postImage = postDocumentSnapshot.getString("postImage"),
            postLikesNumber = postDocumentSnapshot.getLong("likesNumber")!!.toInt(),
            user = user,
            postLiked = getPostLikeStatus(postId)
        )
    }

    suspend fun getUser(userId: String): User {
        val userDocumentSnapshot = db.document("users/${userId}").get().await()

        return User(
            uid = userDocumentSnapshot.id,
            firstName = userDocumentSnapshot.getString("firstName").toString(),
            nickname = userDocumentSnapshot.getString("nickname").toString(),
            dateOfBirth = userDocumentSnapshot.getTimestamp("dateOfBirth"),
            aboutMe = null,
            profilePictureUri = Uri.parse(userDocumentSnapshot.getString("profilePictureUrl").toString())
        )
    }

    private fun getPostLikeStatus(postId: String): Observable<Boolean> {
        val likeDocRef = db.document("posts/$postId/likes/${auth.uid}")
        return Observable.create<Boolean> { emitter ->
            likeDocRef
                .addSnapshotListener { documentSnapshot, firestoreException ->
                    Timber.i("Post id: $postId")
                    if (firestoreException != null) {
                        emitter.onError(firestoreException)
                    } else {
                        if (documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.getBoolean(
                                "exists"
                            )!!
                        ) {
                            emitter.onNext(true)
                        } else {
                            emitter.onNext(false)
                        }
                    }
                }
        }
    }

    fun addNewAdvertisement(advertisement: Advertisement): Task<Void> {
        val data = hashMapOf<String, Any>()

        data["dateCreated"] = FieldValue.serverTimestamp()
        advertisement.filters.game?.let { data["game"] = it }
        advertisement.filters.communicationLanguage?.let { data["communicationLanguage"] = it }
        advertisement.description?.let { data["description"] = it }
        data["createdByUserUid"] = auth.uid!!

        return db.collection("advertisements").document().set(data)
    }

    fun likeThePost(postId: String): Task<Void> {
        val postLikesCollectionRef = db.document("posts/$postId/likes/${auth.uid}")
        val data = mapOf("exists" to true)
        return postLikesCollectionRef.set(data)
    }

    fun unlikeThePost(postId: String): Task<Void> {
        val postLikesCollectionRef = db.document("posts/$postId/likes/${auth.uid}")
        return postLikesCollectionRef.delete()
    }

    suspend fun changeUserProfilePicture(uri: Uri) = withContext(Dispatchers.IO) {
        val userProfileStorageRef = storageReference.child("users/${auth.uid}/")

        val filename = "profile_picture"

        val urlToUpdate = uploadPhotoAndReturnUrl(uri, userProfileStorageRef, filename)
        // Update profile picture url in algolia
        algolia.updateProfilePictureUrl(urlToUpdate)

        updateProfilePictureUrl(urlToUpdate)
    }

    // Uploads picture to the given location in cloud storage and return
    suspend fun uploadPhotoAndReturnUrl(
        uri: Uri,
        storageRef: StorageReference,
        name: String
    ): String {
        val fileRef = storageRef.child(name)
        fileRef.putFile(uri).await()
        // Return url
        return fileRef.downloadUrl.await().toString()
    }

    fun uploadNewComment(postId: String, postContent: String) {
        val commentsCollectionRef = db.collection("posts/$postId/comments")
        val data = HashMap<String, Any>()

        data["content"] = postContent
        data["dateCreated"] = FieldValue.serverTimestamp()
        data["createdByUserId"] = auth.uid!!

//        commentsCollectionRef.document().set(data)
        commentsCollectionRef.add(data)
    }

}