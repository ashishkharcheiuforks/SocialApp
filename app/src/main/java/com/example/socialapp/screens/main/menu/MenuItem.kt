package com.example.socialapp.screens.main.menu

import android.widget.ImageView
import androidx.databinding.BindingAdapter

data class MenuItem(val menuItemTitle: String, val menuItemIcon: Int)

@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, resource: Int){
    imageView.setImageResource(resource)
}
