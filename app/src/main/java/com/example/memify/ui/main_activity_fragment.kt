package com.example.memify.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.memify.Util.NetworkResult
import com.example.memify.Util.image_util.Companion.getBitmapImage
import com.example.memify.Util.image_util.Companion.getLocalBitmapUri
import com.example.memify.databinding.ActivityMainBinding
import com.example.memify.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class main_activity_fragment : Fragment() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() =_binding!!
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=ActivityMainBinding.inflate(inflater,container,false)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        lifecycleScope.launch {
            Log.d("main_fragment","${mainViewModel.memeResponse.value?.data?.url==null}")
            if (mainViewModel.memeResponse.value?.data?.url==null) {
                showShimmerEffect()
                readApiResponse()
            }else{
                hideShimmerEffect()
                binding.mainViewModel=mainViewModel
                binding.executePendingBindings()
            }
        }
        binding.nextButton.setOnClickListener {
            Log.d("main-fragment"," next button yes")
            showShimmerEffect()
            readApiResponse()
        }
        binding.shareButton.setOnClickListener {
            val a = mainViewModel.memeResponse.value
            lifecycleScope.launch {
                val bitmap = getBitmapImage(a?.data?.url,requireContext())
                val uri = getLocalBitmapUri(bitmap,requireContext())
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/*"
                }
                startActivity(Intent.createChooser(shareIntent, "Share this meme through"))
            }
        }
        binding.cardView.setOnClickListener {
            Log.d("main-fragment","yes")
            val directions =main_activity_fragmentDirections.
            actionMainActivityFragmentToDetailsFragment(mainViewModel.memeResponse.value?.data?.url)
            findNavController().navigate(directions)
        }

        return binding.root
    }

    fun readApiResponse() {
        mainViewModel.getMeme()
        mainViewModel.memeResponse.observe(requireActivity(), { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.mainViewModel=mainViewModel
                    binding.executePendingBindings()
                    hideShimmerEffect()
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_LONG).show()
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
        binding.viewline.visibility=View.GONE
    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.viewline.visibility=View.VISIBLE
        binding.shimmerFrameLayout.visibility = View.INVISIBLE
        binding.imageView.visibility = View.VISIBLE
        binding.titleTextView.visibility = View.VISIBLE
        binding.likeImageView.visibility = View.VISIBLE
        binding.likeTextView.visibility = View.VISIBLE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}