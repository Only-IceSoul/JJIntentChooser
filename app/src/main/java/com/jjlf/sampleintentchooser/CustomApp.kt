package com.jjlf.sampleintentchooser

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.security.spec.ECField


class CustomApp : AppCompatActivity() {

    private val mBitmap = Bitmap.createBitmap(400,400,Bitmap.Config.ARGB_8888)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_app)
        val canvas = Canvas(mBitmap)
        canvas.drawColor(Color.RED)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16f,resources.displayMetrics)
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("CustomApp",200f,200f,paint)



        findViewById<ConstraintLayout>(R.id.mainView).setOnClickListener {
            val intentResult = Intent()
            intentResult.putExtra("photo",doImage())
            intentResult.putExtra("custom",1)
            setResult(Activity.RESULT_OK,intentResult)
            finish()
            //here is the finish
        }
    }

    private fun doImage(): ByteArray?{
        return try {
            val stream = ByteArrayOutputStream()
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        }catch (e:Exception){
            Log.e("ERROR","${e.printStackTrace()}")
            null
        }
    }
}
