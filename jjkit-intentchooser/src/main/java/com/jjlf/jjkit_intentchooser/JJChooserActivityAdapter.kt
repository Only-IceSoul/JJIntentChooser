package com.jjlf.jjkit_intentchooser

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.jjlf.jjkit_layoututils.JJScreen


class JJChooserActivityAdapter (
    intentInteractionListener: IntentInteractionListener,
    @param:NonNull @field:NonNull
    private var mIntents: MutableList<ResolveInfo>,
    @param:NonNull @field:NonNull
    private val mPackageManager: PackageManager
) : RecyclerView.Adapter<JJChooserActivityAdapter.ActivityViewHolder>() {

    private var mIntentInteractionListener: IntentInteractionListener? = intentInteractionListener


    override fun getItemCount(): Int {
        return mIntents.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder.newInstance(parent)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = mIntents[position]
        holder.view.imageView.setImageDrawable(activity.loadIcon(mPackageManager))
        holder.view.title.text = activity.loadLabel(mPackageManager)
        holder.view.setOnClickListener {
            mIntentInteractionListener?.onIntentClicked(activity)
        }
        holder.view.setOnLongClickListener {
            return@setOnLongClickListener mIntentInteractionListener?.onIntentLongClicked(activity)
                ?: false
        }
    }


    interface IntentInteractionListener {

        fun onIntentClicked(resolveInfo: ResolveInfo)

        fun onIntentLongClicked(resolveInfo: ResolveInfo): Boolean
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mIntentInteractionListener = null
        super.onDetachedFromRecyclerView(recyclerView)
    }


    class ActivityViewHolder(var view: IntentOptionView) : RecyclerView.ViewHolder(view) {

        companion object {
            fun newInstance(parent: ViewGroup): ActivityViewHolder {
                val v = IntentOptionView(parent.context)
                v.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                return ActivityViewHolder(v)
            }
        }

    }


    class IntentOptionView(context:Context) : LinearLayout(context) {

        val imageView = AppCompatImageView(context)
        val title = AppCompatTextView(context)

        init {
            orientation = VERTICAL

            val imgSize = JJScreen.point(220)
            addView(imageView)
            addView(title)
            val lpImg = LayoutParams(imgSize,imgSize)
            lpImg.gravity = Gravity.CENTER_HORIZONTAL
            lpImg.bottomMargin = JJScreen.point(35)
            imageView.layoutParams = lpImg

            title.textSize = JJScreen.responsiveTextSize(13,0,0,12,11).toFloat()
            title.typeface = Typeface.create("sans-serif-condensed",Typeface.NORMAL)
            title.textAlignment = View.TEXT_ALIGNMENT_CENTER
            title.setTextColor(Color.parseColor("#DD000000"))
            val lpText = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            lpText.gravity = Gravity.CENTER_HORIZONTAL
            title.layoutParams = lpText
            title.maxLines = 2
            title.ellipsize = TextUtils.TruncateAt.END
        }
    }
}

