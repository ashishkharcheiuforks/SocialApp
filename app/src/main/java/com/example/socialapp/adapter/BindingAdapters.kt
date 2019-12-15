package com.example.socialapp.adapter

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.socialapp.GlideApp
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object BindingAdapters {

    // Used for setting image with given uri */
    @BindingAdapter("imageResourceUri")
    @JvmStatic
    fun setImageResource(imageView: ImageView, uri: Uri?) {

        val circularProgressDrawable = CircularProgressDrawable(imageView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        GlideApp.with(imageView.context)
            .load(uri)
            .placeholder(circularProgressDrawable)
            .circleCrop()
            .into(imageView)
    }

    // Displays timestamp as dd/MM/yyyy HH:mm
    @BindingAdapter("dateToFullDateText")
    @JvmStatic
    fun dateToFullDateText(textView: TextView, timestamp: Timestamp) {
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val netDate = Date(milliseconds)
        val date = sdf.format(netDate).toString()
        textView.text = date
    }

    // Displays timestamp as dd/MM/yyyy
    @BindingAdapter("dateToText")
    @JvmStatic
    fun dateToText(
        editText: com.google.android.material.textfield.TextInputEditText,
        timestamp: Timestamp?
    ) {
        if (timestamp != null) {
            val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val netDate = Date(milliseconds)
            val date = sdf.format(netDate).toString()
            editText.setText(date)
        }
    }

}