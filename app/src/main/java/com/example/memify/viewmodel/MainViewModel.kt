package com.example.memify.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.memify.Util.NetworkResult
import com.example.memify.data.Repository
import com.example.memify.models.Meme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    val memeResponse: MutableLiveData<NetworkResult<Meme>> = MutableLiveData()

    fun getMeme() = viewModelScope.launch {
        getMemeSafeCall()
    }

    private suspend fun getMemeSafeCall() {
        memeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getMeme()
                Log.d("mainViewModel", "${response.isSuccessful}")
                memeResponse.value = handleMemeResponse(response)
            } catch (e: Exception) {
                memeResponse.value = NetworkResult.Error("Meme not found")
                e.printStackTrace()
            }
        } else {
            memeResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleMemeResponse(response: Response<Meme>): NetworkResult<Meme> {
        when {
            response.isSuccessful -> {
                return NetworkResult.Success(response.body()!!)
            }
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API key Limited.")
            }
            response.body()!!.url.isNullOrEmpty() -> {
                return NetworkResult.Error("Meme not found.")
            }
            else -> {
                return NetworkResult.Error("I Don't fell so good Mr.Stark")
            }
        }
    }

    fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}