package com.jjlf.jjkit_intentchooser

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.provider.MediaStore


class JJIntentChooser(private val context:Activity) {

    private val tag = "JJIntentChooser"
    private var mTitle = ""
    private var mDrawable: Drawable? = null
    private var mIntent : Intent? = null
    private var mIsForResult = false
    private var mListIgnore = mutableListOf<String>()
    private var mIntentSecondaryList = mutableListOf<Intent>()

    private var mActivityClassIntent: Class<out JJIntentChooserActivity> =
        JJIntentChooserActivity::class.java

    fun setActivity(activityClassIntent: Class<out JJIntentChooserActivity>): JJIntentChooser {
        this.mActivityClassIntent = activityClassIntent
        return this
    }

    fun setTitle(title:String): JJIntentChooser {
        mTitle = title
        return this
    }

    fun setTitle(resId:Int): JJIntentChooser {
        mTitle = context.resources.getString(resId)
        return this
    }

    fun setBackground(drawable: Drawable): JJIntentChooser {
        mDrawable = drawable
        return this
    }

    fun setIntent(intent:Intent): JJIntentChooser {
        mIntent = intent
        return this
    }


    fun setIgnore(vararg activityInfoNames: String): JJIntentChooser {
        activityInfoNames.forEach{
            mListIgnore.add(it)
        }
        return this
    }

    fun setIntentSecondaries(vararg intents:Intent): JJIntentChooser {
        intents.forEach {
            require(!isIncompatibleIntent(it)) { "Incompatible Action - Use same action" }
            mIntentSecondaryList.add(it)
        }
        return this
    }

    private fun isIncompatibleIntent(intent: Intent): Boolean {
        var v = true
        if(mIntent != null){
            if(mIntent!!.action == intent.action) v = false
            if(mIntent!!.action == MediaStore.ACTION_IMAGE_CAPTURE && intent.action == Intent.ACTION_GET_CONTENT) v = false
            if(mIntent!!.action == Intent.ACTION_GET_CONTENT && intent.action == MediaStore.ACTION_IMAGE_CAPTURE) v = false
        }
        return v
    }

    fun start(){
        if (mIntent == null || mIntent?.action == null) {
            Log.e(tag, "Cannot show the share screen - Primary intent is missing or has no action.")
            return
        }
       val intent = setupIntent()
        context.startActivity(intent)
        context.overridePendingTransition(R.anim.intent_chooser_activity,0)
    }

    fun startForResult(requestCode: Int){
        if (mIntent  == null || mIntent?.action == null) {
            Log.e(tag, "Cannot show the share screen - Primary intent is missing or has no action.")
            return
        }
        mIsForResult = true
        val intent = setupIntent()
        context.startActivityForResult(intent,requestCode)
        context.overridePendingTransition(R.anim.intent_chooser_activity,0)
    }

    private fun setupIntent(): Intent {
        val target = Intent(context, mActivityClassIntent)
        target.putExtra(JJIntentChooserActivity.INTENT_KEY, mIntent)
        target.putExtra(JJIntentChooserActivity.RESULT_KEY, mIsForResult)
        if(mListIgnore.isNotEmpty()) target.putExtra(JJIntentChooserActivity.IGNORE_KEY,mListIgnore.toTypedArray())
        if(mIntentSecondaryList.isNotEmpty()) target.putExtra(JJIntentChooserActivity.INTENT_SECONDARY_KEY,mIntentSecondaryList.toTypedArray())
        if(mTitle != "") target.putExtra(JJIntentChooserActivity.TITLE_KEY,mTitle)
        if(mDrawable != null)  {
                JJIntentChooserActivity.BackgroundDrawable = mDrawable
        }
        return target
    }


}