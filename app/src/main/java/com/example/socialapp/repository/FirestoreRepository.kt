package com.example.socialapp.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.socialapp.common.Result
import com.example.socialapp.common.awaitTaskCompletable
import com.example.socialapp.common.awaitTaskResult
import com.example.socialapp.common.getDataFlow
import com.example.socialapp.livedata.DocumentSnapshotLiveData
import com.example.socialapp.livedata.UserLiveData
import com.example.socialapp.model.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber


class FirestoreRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private val algolia = AlgoliaRepository()

    private val defaultProfilePictureUrl =
        "https://firebasestorage.googleapis.com/v0/b/social-app-a3759.appspot.com/o/pr" +
                "ofilepic.jpg?alt=media&token=ad32501b-383e-4a25-b1d2-b3586ee338bd"

    suspend fun createAccount(
        email: String,
        password: String,
        firstName: String,
        nickname: String,
        dateOfBirth: Timestamp
    ): Result<Exception, AuthResult> {

        val createAccountTaskResult = Result.build {
            awaitTaskResult(
                auth.createUserWithEmailAndPassword(email, password)
            )
        }

        return when (createAccountTaskResult) {
            is Result.Error -> {
                createAccountTaskResult
            }
            is Result.Value -> {

                insertUserDataOnRegistration(firstName, nickname, dateOfBirth)

                algolia.insertUser(firstName, nickname, defaultProfilePictureUrl)

                createAccountTaskResult
            }
        }
    }

    suspend fun signIn(
        email: String,
        password: String
    ): Result<Exception, AuthResult> {
        return Result.build {
            awaitTaskResult(
                auth.signInWithEmailAndPassword(email, password)
            )
        }
    }


    suspend fun insertUserDataOnRegistration(
        firstName: String,
        nickname: String,
        dateOfBirth: Timestamp
    ): Result<Exception, Unit> {

        val userProfileInfo = hashMapOf(
            "firstName" to firstName,
            "nickname" to nickname,
            "dateOfBirth" to dateOfBirth,
            "profilePictureUrl" to defaultProfilePictureUrl
        )

        val userDocRef = db.document("users/${auth.uid}")

        return Result.build {
            awaitTaskCompletable(userDocRef.set(userProfileInfo))
        }
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
        profilePictureUrl: String?
    ): Task<Void> {
        val userDocRef = db.document("users/${auth.uid}")

        val data = mutableMapOf<String, Any>()
        // If value exists add it to update data set
        firstName?.let {
            data.put("firstName", it)
        }
        nickname?.let {
            data.put("nickname", it)
        }
        dateOfBirth?.let {
            data.put("dateOfBirth", it)
        }

        profilePictureUrl?.let {
            changeUserProfilePicture(profilePictureUrl)
        }

        algolia.updateNameAndNickname(firstName!!, nickname!!)

        return userDocRef.update(data)
    }

    suspend fun changeUserProfilePicture(filePath: String) {
        val userProfileStorageRef = storageReference.child("users/${auth.uid}/")
        val filename = "profile_picture"
        // uploads picture and returns new url
        val urlToUpdate = uploadPhotoAndReturnUrl(filePath, userProfileStorageRef, filename)
        // Updates url reference to profile picture
        updateProfilePictureUrl(urlToUpdate)
        // Update profile picture url in algolia
        algolia.updateProfilePictureUrl(urlToUpdate)
    }

    // Uploads picture to the given location in cloud storage and return
    suspend fun uploadPhotoAndReturnUrl(
        filePath: String,
        storageRef: StorageReference,
        name: String
    ): String {
        val fileRef = storageRef.child(name)
        val uri = Uri.parse(filePath)
        fileRef.putFile(uri).await()
        return fileRef.downloadUrl.await().toString()
    }

    // Updates reference link to the user profile picture in firestore database
    fun updateProfilePictureUrl(url: String) {
        val userDocRef = db.document("users/${auth.uid}")
        userDocRef.update(mapOf("profilePictureUrl" to url))
    }

    /**    Friend Request related functions    **/

    suspend fun inviteToFriends(uid: String): Result<Exception, Unit> {
        val data = mapOf("status" to FriendshipStatus.INVITATION_SENT.status)
        val myFriends = db.document("users/${auth.uid}/friends/$uid")
        return Result.build { awaitTaskCompletable(myFriends.set(data)) }
    }

    suspend fun acceptFriendRequest(uid: String): Result<Exception, Unit> {
        val data = mapOf("status" to FriendshipStatus.ACCEPTED.status)
        val friendDocRef = db.document("users/${auth.uid}/friends/$uid")
        return Result.build { awaitTaskCompletable(friendDocRef.update(data)) }
    }

    suspend fun deleteFriendRequest(uid: String): Result<Exception, Unit> {
        val friendDocRef = db.document("users/${auth.uid}/friends/$uid")
        return Result.build { awaitTaskCompletable(friendDocRef.delete()) }
    }

    fun getFriendshipStatus(uid: String): DocumentSnapshotLiveData {
        val friendDocRef = db.document("users/${auth.uid}/friends/$uid")
        return DocumentSnapshotLiveData(friendDocRef)
    }

    /**    Posts and comments related functions    **/

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

    private suspend fun getPost(postId: String, userId: String): Post {
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

    suspend fun likePost(postId: String): Result<Exception, Unit> {
        val postLikesCollectionRef = db.document("posts/$postId/likes/${auth.uid}")
        val data = mapOf("exists" to true)
        return Result.build {
            awaitTaskCompletable(postLikesCollectionRef.set(data))
        }
    }

    suspend fun unlikePost(postId: String): Result<Exception, Unit> {
        val postLikesCollectionRef = db.document("posts/$postId/likes/${auth.uid}")
        return Result.build {
            awaitTaskCompletable(postLikesCollectionRef.delete())
        }
    }

    suspend fun uploadNewComment(postId: String, postContent: String): Result<Exception, Unit> {
        val commentsCollectionRef = db.collection("posts/$postId/comments")
        val data = HashMap<String, Any>()

        data["content"] = postContent
        data["dateCreated"] = FieldValue.serverTimestamp()
        data["createdByUserId"] = auth.uid!!

        return Result.build {
            awaitTaskCompletable(
                commentsCollectionRef.add(data)
            )
        }
    }

    @ExperimentalCoroutinesApi
    fun commentsFlow(postId: String): Flow<List<Comment>> {
        val commentsCollectionRef = db.collection("posts/$postId/comments")
        return commentsCollectionRef.getDataFlow { querySnapshot ->
            querySnapshot?.documents?.map {
                it.toObject(Comment::class.java)!!
            } ?: emptyList()
        }
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

    fun uploadPictureAndReturnUrl(pictureToUpload: Uri, path: StorageReference): Task<Uri> {
        val uploadTask = path.putFile(pictureToUpload)

        return uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            return@Continuation path.downloadUrl
        })
    }

    suspend fun getUser(userId: String): User {
        val userDocSnap = db.document("users/${userId}").get().await()
        return userDocSnap.toObject(User::class.java)!!
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

    suspend fun addNewAdvertisement(advertisement: Advertisement): Result<Exception, Unit> {
        val data = hashMapOf<String, Any>()

        data["dateCreated"] = FieldValue.serverTimestamp()
        advertisement.filters.playersNumber?.let { data["playersNumber"] = it }
        advertisement.filters.game?.let { data["game"] = it }
        advertisement.filters.communicationLanguage?.let { data["communicationLanguage"] = it }
        advertisement.description?.let { data["description"] = it }
        data["createdByUserUid"] = auth.uid!!

        return Result.build {
            awaitTaskCompletable(
                db.collection("advertisements").document().set(data)
            )
        }
    }


    // Emits list of user ids that currently have pending friend requests
    @ExperimentalCoroutinesApi
    fun invitesFlow(): Flow<List<String>> {
        val query = db.collection("users/${auth.uid}/friends")
            .whereEqualTo("status", FriendshipStatus.INVITATION_RECEIVED.status)
        return query.getDataFlow { querySnapshot ->
            querySnapshot?.documents?.map {
                it.id
            } ?: emptyList()
        }
    }

    /**    Chat related functions   */

    suspend fun sendTextMessage(roomId: String, text: String): Result<Exception, Unit> {
        val messagesColRef = db.collection("chatRooms/${roomId}/messages")
        val chatRoomDocRef = db.document("chatRooms/${roomId}")
        val batch = db.batch()

        val data = mapOf(
            "text" to text,
            "dateCreated" to FieldValue.serverTimestamp(),
            "createdByUserId" to auth.uid
        )

        batch.set(messagesColRef.document(), data)
        batch.update(chatRoomDocRef, mapOf("lastUpdated" to FieldValue.serverTimestamp()))

        return Result.build {
            awaitTaskCompletable(
                batch.commit()
            )
        }
    }


    suspend fun getChatRoomId(otherUserId: String): String {
        val userId = auth.uid
        val querySnap = db.collection("chatRooms")
            .whereEqualTo("members.$otherUserId", true)
            .whereEqualTo("members.$userId", true)
            .limit(1)
            .get().await()
        return if (!querySnap.isEmpty) querySnap.documents[0].id
        else createChatRoom(otherUserId)
    }

    // Nested field / nested object is created in order to support query that contains two specific users
    // Array is created in order to support compose index in order to query for user chatrooms ordered by "lastUpdated" timestamp field
    private suspend fun createChatRoom(otherUserId: String): String {
        val createRoomTask =
            db.collection("chatRooms").add(
                mapOf(
                    "members" to mapOf(
                        otherUserId to true,
                        auth.uid to true
                    ),
                    "membersArray" to listOf(otherUserId, auth.uid)
                )
            ).await()
        return createRoomTask.id
    }

    @ExperimentalCoroutinesApi
    fun chatMessagesFlow(roomId: String): Flow<List<Message>> {
        val collRef = db.collection("chatRooms/$roomId/messages")
            .orderBy("dateCreated", Query.Direction.ASCENDING)
        return collRef.getDataFlow { querySnapshot ->
            querySnapshot?.documents?.map {
                it.toObject(Message::class.java)!!
            } ?: emptyList()
        }
    }

    suspend fun getChatRooms(
        pageSize: Int,
        loadBefore: String? = null,
        loadAfter: String? = null
    ): List<LastMessage> {
        val colRef = db.collection("chatRooms")
        var query = colRef
            //Cannot create index for an object key it would require creating index for every user for member.$uid
//            .whereEqualTo("members.${auth.uid}", true)
            .whereArrayContains("membersArray", auth.uid as String)
            .orderBy("lastUpdated", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        loadBefore?.let {
            val item = colRef.document(it).get().await()
            query = query.endBefore(item)
        }

        loadAfter?.let {
            val item = colRef.document(it).get().await()
            query = query.startAfter(item)
        }

        return query.get().await().map {
            val message = getLastMessage(it.id)
            val members = it.data["members"] as Map<*, *>
            val uid = members.keys.first { uid -> uid != auth.uid } as String
            val user = getUser(uid)
            LastMessage(it.id, user, message)
        }
    }

    private suspend fun getLastMessage(chatRoomId: String): Message {
        val colRef = db.collection("chatRooms/$chatRoomId/messages")
        val querySnap = colRef
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
        return querySnap.documents[0].toObject(Message::class.java) ?: Message()

    }

}