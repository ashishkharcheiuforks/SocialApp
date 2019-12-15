package com.example.socialapp.screens.login

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.socialapp.livedata.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _isOnLoginSuccessful = SingleLiveEvent<Boolean>()
    val isOnLoginSuccessful: LiveData<Boolean>
        get() = _isOnLoginSuccessful

    // Status of login process being in progress
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // Two way data binding variables that store input of user credentials
    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    // Status of Login button being enabled depending on credentials input validation
    private val _isLoginButtonEnabled = MediatorLiveData<Boolean>()
    val isLoginButtonEnabled: LiveData<Boolean>
        get() = _isLoginButtonEnabled

    init {
        Timber.i("Init called")
        _isLoading.value = false

        // Adding sources to MediatorLiveData
        _isLoginButtonEnabled.addSource(email) { validate() }
        _isLoginButtonEnabled.addSource(password) { validate() }
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        _isLoginButtonEnabled.removeSource(email)
        _isLoginButtonEnabled.removeSource(password)
        super.onCleared()
    }

    fun login() {
        loadingStarted()
        auth.signInWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isOnLoginSuccessful.value = true
                } else {
                    // Sign in fail
                    Timber.d("Sign in failed: ${task.exception?.message}")
                    _isOnLoginSuccessful.value = false
                }
                loadingFinished()
            }
    }

    // Checks if email structure is proper and if password is at least 6 characters
    private fun validate() {
        _isLoginButtonEnabled.value = !(password.value!!.length < 6 || !isEmailValid())
    }

    private fun isEmailValid(): Boolean {
        return !TextUtils.isEmpty(email.value?.trim())
                && android.util.Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()
    }


    private fun loadingStarted() {
        _isLoading.value = true
    }

    private fun loadingFinished() {
        _isLoading.value = false
    }

}