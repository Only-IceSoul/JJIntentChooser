package com.jjlf.sampleintentchooser

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.BufferedInputStream
import java.io.File


object PhotoHelper {


   //maybe not work in huawei p90
    fun getPhotoIntentQuery(): Intent {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "image/*"
        return  i
    }

    fun getCameraIntentQuery(context: Context): Intent {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val  outputFileUri = getOutputUri(context)
        if (outputFileUri != null) {
            i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        }
        return  i
    }

    fun getResultUri(context: Context, data: Intent?): Uri? {
        var isCamera = true
        if (data != null && data.data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera || data!!.data == null) getOutputUri(context) else data.data
    }

    // no work with file provider
    fun getOutputUri(context: Context): Uri? {
        var outputFileUri: Uri? = null
        val getImage = context.externalCacheDir
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "pickImageResult.jpeg"))
        }
        return outputFileUri
    }

    fun getBitmap(context: Context, path: Uri?): Bitmap? {
        return try {
            val inputStream = BufferedInputStream(context.contentResolver.openInputStream(path!!)!!)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("Photo","ERROR: $e")
            null
        }
    }


}