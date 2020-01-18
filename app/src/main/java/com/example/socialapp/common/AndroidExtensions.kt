package com.example.socialapp.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

internal fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

internal fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

internal fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

internal fun Fragment.showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

internal fun Fragment.showSnack(message: String) {
    Snackbar.make(
        activity?.findViewById(android.R.id.content)!!,
        message,
        Snackbar.LENGTH_SHORT
    ).show()
}

// Extension for adding list of sources that triggers the same function in "onchange"
internal fun <T>MediatorLiveData<T>.addSources(sources: List<MutableLiveData<*>>, onChanged: () -> Unit){
    val md = this
    sources.forEach {
        md.addSource(it){ onChanged() }
    }
}

internal fun <T>MediatorLiveData<T>.removeSources(sources: List<MutableLiveData<*>>){
    val md = this
    sources.forEach {
        md.removeSource(it)
    }
}