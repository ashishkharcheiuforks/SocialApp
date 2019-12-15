package com.example.socialapp.common

class Resource<T> private constructor(
    private val data: T?,
    private val error: Exception?
) {

    val isSuccessful: Boolean
        get() = data != null && error == null

    constructor(data: T) : this(data, null)

    constructor(exception: Exception) : this(null, exception)

    fun data(): T {
        if (error != null) {
            throw IllegalStateException("Check isSuccessful first: call error() instead.")
        }
        return data!!
    }

    fun error(): Exception {
        if (data != null) {
            throw IllegalStateException("Check isSuccessful first: call data() instead.")
        }
        return error!!
    }
}