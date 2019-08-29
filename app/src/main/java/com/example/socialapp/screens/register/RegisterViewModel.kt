package com.example.socialapp.screens.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val db = FirebaseFirestore.getInstance()

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean>
        get() = _registerSuccess

    init {
        _registerSuccess.value = null
    }

    fun onRegister(email: String, password: String, confirmPassword: String) {

        if (email.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
            registerFailed(); return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Create user success
                Log.d("RegisterViewModel", "Register successful")

                val user = hashMapOf(
                    "firstName" to "",
                    "nickname" to "",
                    "email" to auth.currentUser!!.email.toString(),
                    "dateOfBirth" to ""
                )

                db.collection("users").document(auth.uid.toString())
                    .set(user)
                    .addOnSuccessListener {
                        Log.d(
                            "RegisterViewModel",
                            "DocumentSnapshot successfully written!"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "RegisterViewModel",
                            "Error writing document",
                            e
                        )
                    }
                    .addOnCompleteListener {
                        auth.signOut()
                        registerSuccess()
                    }
            } else {
                // Create user fail
                Log.d("RegisterViewModel", "Register failed")
                registerFailed()
            }
        }
    }

    private fun registerFailed() {
        _registerSuccess.value = false
        registerDone()
    }

    private fun registerSuccess() {
        _registerSuccess.value = true
        registerDone()
    }

    private fun registerDone() {
        _registerSuccess.value = null
    }


}