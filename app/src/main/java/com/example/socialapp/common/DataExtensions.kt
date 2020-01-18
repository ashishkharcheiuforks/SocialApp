package com.example.socialapp.common

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun <T> awaitTaskResult(task: Task<T>): T = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result!!)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}

//Wraps Firebase/GMS calls
internal suspend fun <T> awaitTaskCompletable(task: Task<T>): Unit =
    suspendCoroutine { continuation ->
        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(Unit)
            } else {
                continuation.resumeWithException(task.exception!!)
            }
        }
    }

@ExperimentalCoroutinesApi
fun Query.getQuerySnapshotFlow(): Flow<QuerySnapshot?> {
    return callbackFlow {
        val listenerRegistration =
            addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    cancel(
                        message = "error fetching collection data",
                        cause = firebaseFirestoreException
                    )
                    return@addSnapshotListener
                }
                offer(querySnapshot)
            }
        awaitClose {
            Timber.d("cancelling the listener on collection")
            listenerRegistration.remove()
        }
    }
}

@ExperimentalCoroutinesApi
fun <T> Query.getDataFlow(mapper: (QuerySnapshot?) -> T): Flow<T> {
    return getQuerySnapshotFlow()
        .map {
            return@map mapper(it)
        }
}