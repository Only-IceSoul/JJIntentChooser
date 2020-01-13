package com.jjlf.sampleintentchooser

import android.content.pm.ResolveInfo
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.jjlf.jjkit_intentchooser.JJIntentChooserActivity

class TrackingIntentChooserActivity : JJIntentChooserActivity() {


    private lateinit var mCurrentResolverInfo : ResolveInfo


    override fun onIntentClicked(resolveInfo: ResolveInfo) {
        mCurrentResolverInfo = resolveInfo

        Log.e("INTENTCHOOSER", "NAME ${resolveInfo.activityInfo.packageName}  ${resolveInfo.activityInfo.name}")

        if (resolveInfo.activityInfo.name.contains("camera", true)) {
            if (PermissionHelper.checkCamera(this)) {
                super.onIntentClicked(resolveInfo)
            } else {
                PermissionHelper.requestCamera(this, 100)
            }
        } else if(resolveInfo.activityInfo.packageName.contains("document", true)) {

            if(PermissionHelper.checkReadWrite(this)) super.onIntentClicked(resolveInfo)
            else PermissionHelper.requestReadWrite(this,200)

        } else super.onIntentClicked(resolveInfo)

    }

        override fun onIntentLongClicked(resolveInfo: ResolveInfo): Boolean {
            Toast.makeText(this,"LONGCLICKED ${resolveInfo.activityInfo.name}",Toast.LENGTH_SHORT).show()
            Log.e("INTENTCHOOSER", "NAME ${resolveInfo.activityInfo.packageName}  ${resolveInfo.activityInfo.name}")
        //do something
        return super.onIntentLongClicked(resolveInfo)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100 || requestCode == 200) {
            if(permissions.isNotEmpty()){
                if (PermissionHelper.shouldShowRequestPermissionRationale(this,permissions[0])) {
                    Toast.makeText(this, "we need permission", Toast.LENGTH_LONG).show()
                    //deny
                } else {
                    if (PermissionHelper.checkPermission(this,permissions[0])) {
                        onIntentClicked(mCurrentResolverInfo)
                    } else {
                        //never ask me
                        AlertDialog.Builder(this)
                            .setTitle("permission title")
                            .setMessage("Hey bro we need permission, agg tmr!")
                            .setNeutralButton("Ok") { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton("Settings") { _, _ ->
                                PermissionHelper.goSettingsApp(this)
                            }.create().show()
                    }
                }
            }
        }

    }

}