package com.example.memify.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.memify.R
import com.example.memify.Util.image_util.Companion.getBitmapImage
import com.example.memify.Util.image_util.Companion.getLocalBitmapUri
import com.example.memify.databinding.ActivityMainBinding
import com.example.memify.databinding.FragmentDetailsFragmentBinding
import kotlinx.coroutines.launch

class details_fragment : Fragment() {

    val args : details_fragmentArgs by navArgs()
    private var _binding: FragmentDetailsFragmentBinding? = null
    private val binding get() =_binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding=FragmentDetailsFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
         val url = args.imageUrlGiven
         binding.memeImageView.load(url){
             crossfade(400)
         }

        binding.shareImageView.setOnClickListener {
            lifecycleScope.launch {
                val bitmap = getBitmapImage(url,requireContext())
                val uri = getLocalBitmapUri(bitmap,requireContext())
                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/*"
                }
                startActivity(Intent.createChooser(shareIntent, "Share this meme through"))
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}