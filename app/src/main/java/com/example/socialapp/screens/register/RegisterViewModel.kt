package com.example.socialapp.screens.register

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.algolia.search.saas.Client
import com.example.socialapp.repository.FirestoreRepository
import com.example.socialapp.livedata.SingleLiveEvent
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import org.json.JSONObject
import timber.log.Timber


class RegisterViewModel : ViewModel() {

    private val apiKey = "02097ce2016d6a6f130949f04093678d"
    private val applicationID = "78M3CITBN7"

    private val auth = FirebaseAuth.getInstance()

    private val repo = FirestoreRepository()

    // Event triggered after successful register
    private val _isRegisterSuccessful = SingleLiveEvent<Boolean>()
    val isRegisterSuccessful: LiveData<Boolean>
        get() = _isRegisterSuccessful

    // Event triggered in response to register failure containing suitable error message
    private val _displayErrorMessage = SingleLiveEvent<String>()
    val displayErrorMessage: LiveData<String>
        get() = _displayErrorMessage


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

    private val _dateOfBirth = MutableLiveData<Timestamp>()
    val dateOfBirth: LiveData<Timestamp>
        get() = _dateOfBirth

    init {
        Timber.i("Init called")

        addSourcesToSignUpButtonEnabledMediatorLiveData()
    }

    override fun onCleared() {
        Timber.i("onCleared() called")
        removeSourcesFromSignUpButtonEnabledMediatorLiveData()
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

    private fun addSourcesToSignUpButtonEnabledMediatorLiveData() {
        // Add Sources to Mediator Live Data
        _signUpButtonEnabled.addSource(email) { validate() }
        _signUpButtonEnabled.addSource(password) { validate() }
        _signUpButtonEnabled.addSource(confirmPassword) { validate() }
        _signUpButtonEnabled.addSource(firstName) { validate() }
        _signUpButtonEnabled.addSource(nickname) { validate() }
        _signUpButtonEnabled.addSource(dateOfBirth) { validate() }
    }

    private fun removeSourcesFromSignUpButtonEnabledMediatorLiveData() {
        // Remove Sources of Mediator Live Data
        _signUpButtonEnabled.removeSource(email)
        _signUpButtonEnabled.removeSource(password)
        _signUpButtonEnabled.removeSource(confirmPassword)
        _signUpButtonEnabled.removeSource(firstName)
        _signUpButtonEnabled.removeSource(nickname)
        _signUpButtonEnabled.removeSource(dateOfBirth)
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

    fun setDate(timestamp: Timestamp) {
        Timber.i("New Date set: $timestamp")
        _dateOfBirth.value = timestamp
    }

    fun createAccount() {
        Timber.i("onCreateAccount() called")
        auth.createUserWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Create user success
                    Timber.i("Register successful")
                    // Insert newly registered user data to firestore database
                    // and if succeeded add this data to Algolia for additional indexing
                    // that is needed for functional search users screen
                    repo.insertUserDataOnRegistration(
                        firstName.value!!,
                        nickname.value!!,
                        dateOfBirth.value!!
                    ).addOnSuccessListener {
                        val initProfilePic =
                            "https://firebasestorage.googleapis.com/v0/b/social-app-a3759.appspot.com/o/profilepic.jpg?alt=media&token=ad32501b-383e-4a25-b1d2-b3586ee338bd"
                        val jsonObject =
                            JSONObject()
                                .put("first_name", firstName.value!!)
                                .put("nickname", nickname.value!!)
                                .put("profile_picture_url", initProfilePic)

                        val client = Client(applicationID, apiKey)
                        val index = client.getIndex("users")

                        index.addObjectAsync(
                            jsonObject,
                            auth.currentUser?.uid.toString(),
                            null
                        )

                        registerSuccess()
                    }
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Timber.e(e, "Registration failed: FirebaseAuthUserCollisionException")
                        _displayErrorMessage.value = "Email already in use"
                        registerFailed()
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        _displayErrorMessage.value = "Password must be at least 6 characters"
                        registerFailed()
                    }
                }
            }
    }

}