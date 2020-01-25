package com.example.socialapp.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.socialapp.R
import com.example.socialapp.module.GlideApp
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object BindingAdapters {

    @BindingAdapter("imageCircular")
    @JvmStatic
    fun setImageCircular(imageView: ImageView, url: String?) {
        GlideApp.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.ic_account_circle_black_24dp)
            .circleCrop()
            .into(imageView)
    }

    // Used for setting image with given uri */
    @BindingAdapter("imageRectangle")
    @JvmStatic
    fun setImageRectangle(imageView: ImageView, url: String?) {
        GlideApp.with(imageView.context)
            .load(url)
            .into(imageView)
    }

    // Displays timestamp as dd/MM/yyyy HH:mm
    @BindingAdapter("dateToFullDateText")
    @JvmStatic
    fun dateToFullDateText(textView: TextView, timestamp: Timestamp?) {
        timestamp?.let {
            val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val netDate = Date(milliseconds)
            val date = sdf.format(netDate).toString()
            textView.text = date
        }
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