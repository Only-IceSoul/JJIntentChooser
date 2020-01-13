package com.jjlf.sampleintentchooser

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat

object PermissionHelper {


    fun checkCamera(context: Activity): Boolean{
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun checkReadWrite(context: Activity):Boolean{
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCamera(context: Activity,requestCode:Int){
        ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), requestCode)
    }

    fun requestReadWrite(context: Activity,requestCode:Int){
        ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
    }

    fun shouldShowRequestPermissionRationale(activity: Activity,permission:String): Boolean{
        var v = true
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) v = activity.shouldShowRequestPermissionRationale(permission)
        return v
    }

    fun checkPermission(activity: Activity,permission: String):Boolean{
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }


    fun goSettingsApp(activity: Activity){
        val packageUri = Uri.fromParts("package", activity.packageName, null)
        val applicationDetailsSettingsIntent = Intent()
        applicationDetailsSettingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        applicationDetailsSettingsIntent.data = packageUri
        applicationDetailsSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(applicationDetailsSettingsIntent)
    }


}