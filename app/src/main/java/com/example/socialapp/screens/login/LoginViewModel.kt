package com.example.socialapp.screens.login

import android.text.TextUtils
import androidx.lifecycle.*
import com.example.socialapp.common.Result
import com.example.socialapp.livedata.SingleLiveEvent
import com.example.socialapp.repository.FirestoreRepository
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    private val _isOnLoginSuccessful = SingleLiveEvent<Boolean>()
    val isOnLoginSuccessful: LiveData<Boolean>
        get() = _isOnLoginSuccessful

    // Status of login process being in progress
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

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
        viewModelScope.launch {
            loadingStarted()
            val signInTaskResult = repo.signInWithEmailAndPassword(email.value!!, password.value!!)
            when (signInTaskResult) {
                is Result.Error -> {
                    when (signInTaskResult.error) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            displayErrorMessage("Wrong password")
                        }
                        is FirebaseTooManyRequestsException -> {
                            displayErrorMessage("Too many failed login attempts. Try again later")
                        }
                        else -> {
                            signInTaskResult.error.message?.let { displayErrorMessage(it) }
                        }
                    }
                }
                is Result.Value -> {
                    loginSuccessful()
                }
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

    private fun loginSuccessful() {
        _isOnLoginSuccessful.value = true
    }

    private fun loadingStarted() {
        _isLoading.value = true
    }

    private fun loadingFinished() {
        _isLoading.value = false
    }

    private fun displayErrorMessage(message: String) {
        _errorMessage.value = message
    }
}