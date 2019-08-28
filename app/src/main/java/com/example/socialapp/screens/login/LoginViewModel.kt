package com.example.socialapp.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _onLoginSuccess = MutableLiveData<Boolean>()
    val onLoginSuccess: LiveData<Boolean>
        get() = _onLoginSuccess

    init {
        _onLoginSuccess.value = null
    }

    fun onLogin(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            loginFailed(); return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d("LoginViewModel", "Sign in successful")
                    loginSuccess()

                } else {
                    // Sign in fail
                    Log.d("LoginViewModel", "Sign in failed: ${task.exception?.message}")
                    loginFailed()
                }
                // ...
            }
    }

    private fun loginFailed() {
        _onLoginSuccess.value = false
        loginDone()
    }

    private fun loginSuccess() {
        _onLoginSuccess.value = true
        loginDone()
    }

    private fun loginDone() {
        _onLoginSuccess.value = null
    }

}