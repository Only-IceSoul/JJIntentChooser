package com.jjlf.sampleintentchooser

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.jjlf.jjkit_intentchooser.JJIntentChooser

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val customIntent= Intent(this,CustomApp::class.java)
        customIntent.action = Intent.ACTION_GET_CONTENT

        findViewById<Button>(R.id.buttonA).setOnClickListener {

            JJIntentChooser(this)
                .setTitle("Pick app")
                .setIntent(PhotoHelper.getCameraIntentQuery(this))
                .setIntentSecondaries(
                    PhotoHelper.getPhotoIntentQuery(),customIntent,  PhotoHelper.getPhotoIntentQuery()
                    ,  PhotoHelper.getPhotoIntentQuery(),PhotoHelper.getPhotoIntentQuery(),PhotoHelper.getPhotoIntentQuery()
                    ,PhotoHelper.getPhotoIntentQuery(),PhotoHelper.getPhotoIntentQuery()
                )
                .setIgnore("com.google.android.apps.photos.picker.external.ExternalPickerActivity")
                .setActivity(TrackingIntentChooserActivity::class.java)   //add theme chooserActivity to trackingactivity
                .startForResult(100)



        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            val uri = PhotoHelper.getResultUri(this,data)
            val who = data?.getIntExtra("custom",0)
            val image = if(who == 1){
                val byteArray = data.getByteArrayExtra("photo")
                 BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

            }else {
                 PhotoHelper.getBitmap(this,uri)
            }

            findViewById<ImageView>(R.id.imageView).setImageBitmap(image)


        }

        super.onActivityResult(requestCode, resultCode, data)
    }


}
