package com.example.memify.data

import com.example.memify.models.Meme
import retrofit2.Response
import retrofit2.http.GET

interface memeApi {

      @GET("/gimme")
      suspend fun getMeme() : Response<Meme>
}