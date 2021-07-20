package com.example.memify.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memify.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meme_fragment)
    }


}