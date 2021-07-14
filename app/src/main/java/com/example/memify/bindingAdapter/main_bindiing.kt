package com.example.memify.bindingAdapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.example.memify.R

class main_bindiing {

    companion object {
        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            imageView.load(imageUrl) {
                crossfade(400)
                error(R.drawable.ic_error)
            }
        }
    }
}