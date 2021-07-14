package com.example.memify.data

import com.example.memify.models.Meme
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val memeApi: memeApi) {


    suspend fun getMeme(): Response<Meme> {
        return memeApi.getMeme()
    }
}