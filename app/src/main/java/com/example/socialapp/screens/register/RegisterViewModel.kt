package com.example.socialapp.screens.register

import android.text.TextUtils
import androidx.lifecycle.*
import com.example.socialapp.common.Result
import com.example.socialapp.common.addSources
import com.example.socialapp.common.removeSources
import com.example.socialapp.livedata.SingleLiveEvent
import com.example.socialapp.repository.FirestoreRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch
import timber.log.Timber


class RegisterViewModel : ViewModel() {

    private val repo = FirestoreRepository()

    // Event triggered after successful register
    private val _isRegisterSuccessful = SingleLiveEvent<Boolean>()
    val isRegisterSuccessful: LiveData<Boolean>
        get() = _isRegisterSuccessful

    // Event triggered in response to register failure containing suitable error message
    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage


    // Status of register button being enabled depending on inputs being valid
    private val _signUpButtonEnabled = MediatorLiveData<Boolean>()
    val signUpButtonEnabled: LiveData<Boolean>
        get() = _signUpButtonEnabled

    /**
     *     Two-way data binding variables that holds register form inputs
     */
    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")
    val confirmPassword = MutableLiveData<String>("")
    val firstName = MutableLiveData<String>("")
    val nickname = MutableLiveData<String>("")
    val dateOfBirth = MutableLiveData<Timestamp>()

    private val mediatorLiveDataSources = listOf(
        email, password, confirmPassword, firstName, nickname, dateOfBirth
    )

    init {
        Timber.i("Init called")
        _signUpButtonEnabled.addSources(mediatorLiveDataSources) { validate() }
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        _signUpButtonEnabled.removeSources(mediatorLiveDataSources)
        super.onCleared()
    }

    // Validates register form and makes signUpButtonEnabled variable true/false
    private fun validate() {
        _signUpButtonEnabled.value =
            password.value!!.length > 5
                    && password.value!! == confirmPassword.value!!
                    && firstName.value!!.trim().isNotEmpty()
                    && nickname.value!!.trim().isNotEmpty()
                    && isEmailValid()
                    && dateOfBirth.value != null
    }

    // Checks if email matches the common pattern
    private fun isEmailValid(): Boolean {
        return !TextUtils.isEmpty(email.value?.trim()) && android.util.Patterns.EMAIL_ADDRESS.matcher(
            email.value!!
        ).matches()
    }

    private fun registerSuccess() {
        _isRegisterSuccessful.value = true
    }

    private fun registerFailed() {
        _isRegisterSuccessful.value = false
    }

    fun createAccount() {
        viewModelScope.launch {
            val createAccountTaskResult = repo.createAccount(
                email.value!!,
                password.value!!,
                firstName.value!!,
                nickname.value!!,
                dateOfBirth.value!!
            )
            when (createAccountTaskResult) {
                is Result.Error -> {
                    registerFailed()
                    when (createAccountTaskResult.error) {
                        is FirebaseAuthWeakPasswordException -> {
                            displayErrorMessage("Password too weak")
                        }
                        is FirebaseAuthUserCollisionException -> {
                            displayErrorMessage("Email is already in use")
                        }
                        is FirebaseNetworkException -> {
                            displayErrorMessage("Network Error, Try again later")
                        }
                    }
                }
                is Result.Value -> {
                    registerSuccess()
                }
            }

        }
    }

    private fun displayErrorMessage(message: String) {
        _errorMessage.value = message
    }

}