package com.example.memify.ui


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.memify.Util.NetworkResult
import com.example.memify.databinding.ActivityMainBinding
import com.example.memify.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        showShimmerEffect()
        lifecycleScope.launch {
            readApiResponse()
        }
        binding.nextButton.setOnClickListener {
            showShimmerEffect()
            readApiResponse()
        }
        binding.shareButton.setOnClickListener {
            val a = mainViewModel.memeResponse.value
            lifecycleScope.launch {
                val bitmap = getBitmapImage(a?.data?.url)
                val uri = getLocalBitmapUri(bitmap)
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/*"
                }
                startActivity(Intent.createChooser(shareIntent, "Share this meme through"))
            }
        }
    }

    private fun getLocalBitmapUri(bitm: Bitmap): Uri? {
        var bitmap: Uri? = null
        try {
            val strictMode = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(strictMode.build())
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share meme" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bitm.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            bitmap = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    private suspend fun getBitmapImage(url: String?): Bitmap {
        val loading = ImageLoader(this)
        val request = ImageRequest.Builder(this).data(url).build()
        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    fun readApiResponse() {
        mainViewModel.getMeme()
        mainViewModel.memeResponse.observe(this, { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.imageView.load(response.data?.url)
                    binding.titleTextView.text = response.data?.title
                    binding.likeTextView.text = response.data?.ups.toString()
                    hideShimmerEffect()
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(this, response.message.toString(), Toast.LENGTH_LONG).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        })
    }

    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.imageView.visibility = View.GONE
        binding.titleTextView.visibility = View.GONE
        binding.likeImageView.visibility = View.GONE
        binding.likeTextView.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.INVISIBLE
        binding.imageView.visibility = View.VISIBLE
        binding.titleTextView.visibility = View.VISIBLE
        binding.likeImageView.visibility = View.VISIBLE
        binding.likeTextView.visibility = View.VISIBLE
    }
}