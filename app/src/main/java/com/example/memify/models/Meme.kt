package com.example.memify.models


import com.google.gson.annotations.SerializedName

data class Meme(
    @SerializedName("postLink")
    val postLink: String,
    @SerializedName("spoiler")
    val spoiler: Boolean,
    @SerializedName("title")
    val title: String,
    @SerializedName("ups")
    val ups: Int,
    @SerializedName("url")
    val url: String?
)