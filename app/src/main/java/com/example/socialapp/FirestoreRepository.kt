package com.example.socialapp.screens

import android.net.Uri
import com.algolia.search.saas.Client
import com.example.socialapp.DocumentSnapshotLiveData
import com.example.socialapp.FriendshipStatus
import com.example.socialapp.QuerySnapshotLiveData
import com.example.socialapp.model.Post
import com.example.socialapp.model.User
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
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import timber.log.Timber


class FirestoreRepository {

    private val auth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()

    private val storage = FirebaseStorage.getInstance()

    private val storageReference = storage.reference


    fun insertUserDataOnRegistration(
        firstName: String,
        nickname: String,
        dateOfBirth: Timestamp
    ): Task<Void> {

        val newUserProfilePictureUrl =
            "https://firebasestorage.googleapis.com/v0/b/social-app-a3759.appspot.com/o/profilepic.jpg?alt=media&token=ad32501b-383e-4a25-b1d2-b3586ee338bd"

        val userInfo = hashMapOf(
            "firstName" to firstName,
            "nickname" to nickname,
            "dateOfBirth" to dateOfBirth,
            "profilePictureUrl" to newUserProfilePictureUrl
        )

        val userDocRef = db.collection("users").document(auth.uid!!)

        return userDocRef.set(userInfo)
    }

    /**    Replaces current profile picture with new one and updates its new link     **/

    fun changeUserProfilePicture(uri: Uri) {
        val ref =
            storageReference.child("users/${auth.uid}/profile_picture")
        val uploadTask = ref.putFile(uri)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            return@Continuation ref.downloadUrl
        })
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> {
                        Timber.d("changeUserProfilePicture() successful - new picture uri set")
                        Timber.d("New picture Uri: %s", task.result.toString())
                        updateProfilePictureUrl(task.result!!)

                        //Algolia profile picture url update

                        val apiKey = "02097ce2016d6a6f130949f04093678d"
                        val applicationID = "78M3CITBN7"

                        val client = Client(applicationID, apiKey)
                        val index = client.getIndex("users")

                        val jsonObject =
                            JSONObject().put("profile_picture_url", task.result.toString())

                        index.partialUpdateObjectAsync(
                            jsonObject,
                            auth.uid.toString(),
                            null
                        )
                    }
                    else -> {
                        //Task failed
                    }
                }
            }
    }

    /**    Updates link of the user profile picture - not picture itself    **/

    fun updateProfilePictureUrl(uri: Uri) {
        val data = hashMapOf<String, Any>(
            "profilePictureUrl" to uri.toString()
        )
        db.collection("users").document(auth.currentUser!!.uid)
            .update(data)
            .addOnSuccessListener {
                Timber.i("Updated user profile picture url")
            }
            .addOnFailureListener {
                Timber.e(it, "Failed to update user profile picture url")
            }
    }

    /**    Updates user profile with provided non-null values**/

    fun updateUserProfileInfo(
        firstName: String?,
        nickname: String?,
        dateOfBirth: Timestamp?,
        profilePictureUri: Uri?
    ): Task<Void> {
        // Reference to user profile document
        val userDocRef = db.collection("users").document(auth.uid!!)

        // Data set with fields to update in user document
        val data = mutableMapOf<String, Any>()

        // If value exists add it to update data set
        firstName?.let { data.put("firstName", it) }
        nickname?.let { data.put("nickname", it) }
        dateOfBirth?.let { data.put("dateOfBirth", it) }
        profilePictureUri?.let {
            changeUserProfilePicture(profilePictureUri)
        }

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

        val myFriends = db.collection("users")
            .document(auth.uid.toString())
            .collection("friends")
            .document(uid)

        return myFriends.update(data)
    }

    /**    Cancel pending friend request    **/

    fun deleteFriendRequest(uid: String): Task<Void> {

        val myFriends = db.collection("users")
            .document(auth.uid.toString())
            .collection("friends")
            .document(uid)

        return myFriends.delete()
    }

    /**    Snapshot for real-time friendship status    **/

    fun getFriendshipStatus(uid: String): DocumentSnapshotLiveData {
        val docRef =
            FirebaseFirestore.getInstance().collection("users")
                .document(auth.uid.toString())
                .collection("friends")
                .document(uid)
        return DocumentSnapshotLiveData(docRef)
    }

    fun fetchInvitesLiveData(): QuerySnapshotLiveData {
        val query =
            db.collection("users").document(auth.uid.toString())
                .collection("friends")
                .whereEqualTo("status", FriendshipStatus.INVITATION_RECEIVED.status)
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
        postContent?.let {
            data.put("postContent", it)
        }

        // If image was passed to add to the document
        // it will be uploaded
        postImage?.let {
            //TODO(DEV): create random name for the uploaded file
            val postImagesReference = storageReference.child("savedPosts/$newPostId/images/")

            uploadPictureAndReturnUrl(it, postImagesReference).addOnCompleteListener { task ->
                if (task.isSuccessful)
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


    /**
     * Uploads file to the Firebase Storage
     */

    /**
     *  Uploads Picture to the provided path in Firebase Storage and returns Task
     *  With newly uploaded picture Url as a result
     */
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
        db
            .collection("posts")
            .document(postId)
            .update(mapOf("postImage" to imageUrl))
    }


    suspend fun getUserTimeline(
        userUid: String,
        pageSize: Int,
        loadBefore: String? = null,
        loadAfter: String? = null
    ): List<Post> {
        var query = db.collection("users").document(userUid).collection("timeline")
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        loadBefore?.let {
            val item =
                db.collection("users")
                    .document(userUid)
                    .collection("timeline")
                    .document(it)
                    .get()
                    .await()

            query = query.endBefore(item)
        }

        loadAfter?.let {
            val item = db.collection("users")
                .document(userUid)
                .collection("timeline")
                .document(it)
                .get()
                .await()
            query = query.startAfter(item)
        }

        return query.get().await().map {
            getPost(it.id, it.getString("createdByUserUid").toString())
        }
    }


    suspend fun getUserPosts(
        userUid: String,
        pageSize: Int,
        loadBefore: String? = null,
        loadAfter: String? = null
    ): List<Post> {
        val user = getUser(userUid)
        var query = db.collection("posts")
            .whereEqualTo("createdByUserUid", userUid)
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())


        loadBefore?.let {
            val item =
                db.collection("posts")
                    .document(it)
                    .get()
                    .await()

            query = query.endBefore(item)
        }

        loadAfter?.let {
            val item = db.collection("posts")
                .document(it)
                .get()
                .await()
            query = query.startAfter(item)
        }

        return query.get().await().map {
            Post(
                postId = it.id,
                postLikesNumber = it.getLong("likesNumber")!!.toInt(),
                postImage = it.get("postImage")?.let { it -> Uri.parse(it.toString()) },
                postDateCreated = it.getTimestamp("dateCreated")!!,
                postContent = it.get("postContent").toString(),
                postCommentsNumber = it.getLong("commentsNumber")!!.toInt(),
                user = user,
                postLiked = getPostLikeStatus(it.id)
            )
        }
    }

    suspend fun getPost(postId: String, userId: String): Post {
        val user = getUser(userId)
        val postDocumentSnapshot = db.collection("posts").document(postId).get().await()

        return Post(
            postId = postId,
            postCommentsNumber = postDocumentSnapshot.getLong("commentsNumber")!!.toInt(),
            postContent = postDocumentSnapshot.getString("postContent").toString(),
            postDateCreated = postDocumentSnapshot.getTimestamp("dateCreated")!!,
            postImage = null,
            postLikesNumber = postDocumentSnapshot.getLong("likesNumber")!!.toInt(),
            user = user,
            postLiked = getPostLikeStatus(postId)
        )
    }

    suspend fun getUser(userId: String): User {
        val userDocumentSnapshot = db.collection("users").document(userId).get().await()
        return User(
            uid = userDocumentSnapshot.id,
            firstName = userDocumentSnapshot.getString("firstName").toString(),
            nickname = userDocumentSnapshot.getString("nickname").toString(),
            dateOfBirth = userDocumentSnapshot.getTimestamp("dateOfBirth"),
            aboutMe = null,
            profilePictureUri = Uri.parse(userDocumentSnapshot.getString("profilePictureUrl").toString())
        )
    }

    private fun getPostLikeStatus(postId: String): Observable<Boolean> =
        Observable.create<Boolean> { emitter ->
            db
                .collection("posts")
                .document(postId)
                .collection("likes")
                .document(auth.uid.toString())
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
                            Timber.i("Like status: ${documentSnapshot.getBoolean("exists")} cache: ${documentSnapshot.metadata.isFromCache}")
                        } else {
                            emitter.onNext(false)
                            if (documentSnapshot != null) {
                                Timber.i("Like null| cache: ${documentSnapshot.metadata.isFromCache}")
                            }

                        }
                    }
                }
        }

    fun likeThePost(postId: String): Task<Void> {
        val postLikesCollectionRef =
            db.collection("posts/$postId/likes").document(auth.uid!!)
        val data = mapOf("exists" to true)
        return postLikesCollectionRef.set(data)
    }

    fun unlikeThePost(postId: String): Task<Void> {
        val postLikesCollectionRef =
            db.collection("posts/$postId/likes").document(auth.uid!!)
        return postLikesCollectionRef.delete()
    }

}