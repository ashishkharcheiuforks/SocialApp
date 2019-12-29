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
import com.google.firebase.firestore.ListenerRegistration
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
            "https://firebasestorage.googleapis.com/v0/b/social-app-a3759.appspot.com/o/pr" +
                    "ofilepic.jpg?alt=media&token=ad32501b-383e-4a25-b1d2-b3586ee338bd"
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

    // Updates user profile with provided non-null values
    suspend fun updateUserProfileInfo(
        firstName: String?,
        nickname: String?,
        dateOfBirth: Timestamp?,
        profilePictureUri: String?
    ): Task<Void> {
        return withContext(Dispatchers.IO) {
            val userDocRef = db.document("users/${auth.uid}")

            // Data set with fields to update in user document
            val data = mutableMapOf<String, Any>()
            // If value exists add it to update data set
            firstName?.let { data.put("firstName", it) }
            nickname?.let { data.put("nickname", it) }
            dateOfBirth?.let { data.put("dateOfBirth", it) }

            profilePictureUri?.let { changeUserProfilePicture(profilePictureUri) }

            algolia.updateNameAndNickname(firstName!!, nickname!!)

            userDocRef.update(data)
        }
    }

    suspend fun changeUserProfilePicture(url: String) = withContext(Dispatchers.IO) {
        val userProfileStorageRef = storageReference.child("users/${auth.uid}/")
        val filename = "profile_picture"
        val urlToUpdate = uploadPhotoAndReturnUrl(url, userProfileStorageRef, filename)
        updateProfilePictureUrl(urlToUpdate)
        // Update profile picture url in algolia
        algolia.updateProfilePictureUrl(urlToUpdate)
    }

    // Uploads picture to the given location in cloud storage and return
    suspend fun uploadPhotoAndReturnUrl(url: String, storageRef: StorageReference, name: String): String {
        val fileRef = storageRef.child(name)
        val uri = Uri.parse(url)
        fileRef.putFile(uri).await()
        // Return url
        return fileRef.downloadUrl.await().toString()
    }

    // Updates reference link to the user profile picture in firestore database
    fun updateProfilePictureUrl(url: String) {
        val userDocRef = db.document("users/${auth.uid}")
        userDocRef.update(mapOf("profilePictureUrl" to url))
    }

    /**    Friend Request related functions    **/

    fun inviteToFriends(uid: String): Task<Void> {
        val data = mapOf<String, Any>("status" to FriendshipStatus.INVITATION_SENT.status)

        val myFriends = db.collection("users")
            .document(auth.uid!!)
            .collection("friends")
            .document(uid)

        return myFriends.set(data)
    }

    fun acceptFriendRequest(uid: String): Task<Void> {
        val data = mapOf<String, Any>("status" to FriendshipStatus.ACCEPTED.status)
        val friendDocRef = db.document("users/${auth.uid}/friends/$uid")
        return friendDocRef.update(data)
    }

    fun deleteFriendRequest(uid: String): Task<Void> {
        val friendDocRef = db.document("users/${auth.uid}/friends/$uid")
        return friendDocRef.delete()
    }

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
            val postImagesReference = storageReference.child("posts/$newPostId/images/image.jpeg")

            uploadPictureAndReturnUrl(it, postImagesReference).addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Timber.d("Uploaded and returned new picture url: ${task.result}")
                updatePostImageUrl(newPostId, task.result!!)
            }
        }

        newPostDocRef.set(data)
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
            getPost(it.id, it.getString("createdByUserUid")!!)
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

        return query.get().await().map { getUser(it.id) }
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
                dateCreated = it.getTimestamp("dateCreated")!!,
                content = it.get("postContent").toString(),
                postCommentsNumber = it.getLong("commentsNumber")!!.toInt(),
                user = user,
                postLiked = getPostLikeStatus(it.id)
            )
        }
    }

    suspend fun getPost(postId: String, userId: String): Post {
        val user = getUser(userId)
        val postDocSnap = db.document("posts/$postId").get().await()

        return Post(
            postId = postId,
            postCommentsNumber = postDocSnap.getLong("commentsNumber")!!.toInt(),
            content = postDocSnap.getString("postContent").toString(),
            dateCreated = postDocSnap.getTimestamp("dateCreated")!!,
            postImage = postDocSnap.getString("postImage"),
            postLikesNumber = postDocSnap.getLong("likesNumber")!!.toInt(),
            user = user,
            postLiked = getPostLikeStatus(postId)
        )
    }

    suspend fun getUser(userId: String): User = withContext(Dispatchers.IO) {
        val userDocSnap = db.document("users/${userId}").get().await()
        userDocSnap.toObject(User::class.java)!!
    }

    private fun getPostLikeStatus(postId: String): Observable<Boolean> {
        val likeDocRef = db.document("posts/$postId/likes/${auth.uid}")
        return Observable.create<Boolean> { emitter ->
            likeDocRef
                .addSnapshotListener { documentSnapshot, firestoreException ->
                    if (firestoreException != null) {
                        emitter.onError(firestoreException)
                    } else {
                        if (documentSnapshot != null
                            && documentSnapshot.exists()
                            && documentSnapshot.getBoolean("exists")!!
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
        advertisement.filters.playersNumber?.let { data["playersNumber"] = it }
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

    fun uploadNewComment(postId: String, postContent: String) {
        val commentsCollectionRef = db.collection("posts/$postId/comments")
        val data = HashMap<String, Any>()

        data["content"] = postContent
        data["dateCreated"] = FieldValue.serverTimestamp()
        data["createdByUserId"] = auth.uid!!

        commentsCollectionRef.add(data)
    }

    fun addCommentsListener(
        postId: String,
        onListen: (List<Comment>) -> Unit
    ): ListenerRegistration {
        val commentsCollectionRef = db.collection("posts/$postId/comments")

        return commentsCollectionRef
            .orderBy("dateCreated")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Timber.e(firebaseFirestoreException, "CommentsListener error.")
                    return@addSnapshotListener
                }

                val comments = mutableListOf<Comment>()
                querySnapshot!!.documents.forEach {
                    comments.add(it.toObject(Comment::class.java)!!)
                    return@forEach
                }
                onListen(comments)
            }
    }

}