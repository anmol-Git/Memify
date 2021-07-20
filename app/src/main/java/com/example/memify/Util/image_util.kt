package com.example.memify.Util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class image_util {
    companion object{
        fun getLocalBitmapUri(bitm: Bitmap,context : Context): Uri? {
            var bitmap: Uri? = null
            try {
                val strictMode = StrictMode.VmPolicy.Builder()
                StrictMode.setVmPolicy(strictMode.build())
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
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

        suspend fun getBitmapImage(url: String?,context: Context): Bitmap {
            val loading = ImageLoader(context)
            val request = ImageRequest.Builder(context).data(url).build()
            val result = (loading.execute(request) as SuccessResult).drawable
            return (result as BitmapDrawable).bitmap
        }
    }
}