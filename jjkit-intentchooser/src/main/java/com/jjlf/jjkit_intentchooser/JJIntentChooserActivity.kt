package com.jjlf.jjkit_intentchooser

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jjlf.jjkit_layoututils.JJLayout
import com.jjlf.jjkit_layoututils.JJMargin
import com.jjlf.jjkit_layoututils.JJScreen

open class JJIntentChooserActivity : AppCompatActivity(),
    JJChooserActivityAdapter.IntentInteractionListener {


    companion object{
        const val INTENT_KEY = "INTENT_KEY"
        const val TITLE_KEY = "TITLE_KEY"
        const val RESULT_KEY = "RESULT_KEY"
        const val IGNORE_KEY = "IGNORE_KEY"
        const val INTENT_SECONDARY_KEY =  "INTENT_SECONDARY_KEY"
        internal var BackgroundDrawable :Drawable? = null
    }

    private lateinit var mMainView : MotionLayout
    private lateinit var mBackgroundView : View
    private lateinit var mBackgroundIntentsView : ConstraintLayout
    private lateinit var mTittleTextView : AppCompatTextView
    private lateinit var mRecyclerView: ChooserRecycler
    private val mMapOfIntents = mutableMapOf<String,Intent>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mMainView = MotionLayout(this)

        setContentView(mMainView)
        mMainView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
        mMainView.loadLayoutDescription(R.xml.scene_chooser_activity)
        mMainView.setTransition(R.id.start,R.id.end)

        mMainView.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionTrigger(motionLayout: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
            override fun onTransitionStarted(motionLayout: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                when(currentId){
                    R.id.finish -> finish() }
            }
        })

        val primaryIntent:Intent = intent.getParcelableExtra(INTENT_KEY) ?: Intent()

        mIsActivityForResult = intent.getBooleanExtra(RESULT_KEY,false)

        var listIntents = packageManager.queryIntentActivities(primaryIntent, 0)

        listIntents.forEach {
            mMapOfIntents[it.activityInfo.name] = primaryIntent
        }

        intent.getParcelableArrayExtra(INTENT_SECONDARY_KEY)?.forEach { pIntent ->
            val int = pIntent as Intent
            val list = packageManager.queryIntentActivities(int, 0)
            list.forEach{
                mMapOfIntents[it.activityInfo.name] = int
            }
            listIntents.addAll(list)
        }

        val listIgnore = intent.getStringArrayExtra(IGNORE_KEY)
        if(listIgnore != null) {
            listIntents  = listIntents.filter {
                !listIgnore.contains(it.activityInfo.name)
            }
        }

         listIntents  = listIntents.filter {
            it.activityInfo.name != "com.android.fallback.Fallback"
        }

        var startMarginTop = when(listIntents.size){
            in Int.MIN_VALUE..4 -> JJScreen.responsiveSize(JJScreen.percentHeight(0.69f),0,0,0)
            in 5..8 ->  JJScreen.responsiveSize(JJScreen.percentHeight(0.6f),0,0,0)
            in 9..Int.MAX_VALUE -> JJScreen.responsiveSize(JJScreen.percentHeight(0.55f),0,0,0)
            else ->  JJScreen.percentHeight(0.55f)
        }

        val isNeededScroll = when(listIntents.size){
            in Int.MIN_VALUE..4 -> false
            in 5..8 -> false
            in 9..Int.MAX_VALUE -> true
            else -> true
        }


        var startPaddingTopRecycler = when(listIntents.size){
            in Int.MIN_VALUE..4 ->   JJScreen.point(350)
            else ->   JJScreen.point(280)
        }

         val text = intent.getStringExtra(TITLE_KEY)

        if(text == null) {
            val point =  JJScreen.point(150)
            startPaddingTopRecycler -= point
            startMarginTop += point
        }

        val spanGridLayout = when(listIntents.size){
            1 -> 2
            2 -> 2
            3 -> 3
            else -> 4
        }

        setupBackground()
        setupBackgroundIntents(startMarginTop)
        setupTitle(text)
        setupRecyclerView(listIntents,startMarginTop,startPaddingTopRecycler,spanGridLayout)


       if(!isNeededScroll) mMainView.setTransition(R.id.start,R.id.start)

    }



    private fun setupBackgroundIntents(startMarginTop: Int){
        val csStart = mMainView.getConstraintSet(R.id.start)
        val csEnd = mMainView.getConstraintSet(R.id.end)
        val csFinish = mMainView.getConstraintSet(R.id.finish)

        val bgDrawable = BackgroundDrawable ?: ChooserDrawable().ssRadius(JJScreen.point(100).toFloat(),JJScreen.point(100).toFloat(),0f,0f)

        mBackgroundIntentsView = ConstraintLayout(this)
        mBackgroundIntentsView.id = View.generateViewId()
        mMainView.addView(mBackgroundIntentsView)

        mBackgroundIntentsView.background = bgDrawable

        JJLayout.mlSetView(mBackgroundIntentsView)

        JJLayout.mlSetConstraint(csStart)
        JJLayout.mlBottomToBottomParent(0)
        JJLayout.mlFillParentHorizontally()
        JJLayout.mlTopToTopParent(startMarginTop)
        JJLayout.mlApply()

        JJLayout.mlSetConstraint(csEnd)
        JJLayout.mlBottomToBottomParent(0)
        JJLayout.mlFillParentHorizontally()
        JJLayout.mlTopToTopParent(JJScreen.percentHeight(0.18f))

        JJLayout.mlSetConstraint(csFinish)
        JJLayout.mlBottomToBottomParent(0)
        JJLayout.mlFillParentHorizontally()
        JJLayout.mlTopToTopParent(JJScreen.percentHeight(0.95f))

        JJLayout.mlDisposeConstraint()
        JJLayout.mlDisposeView()

    }

    private fun setupRecyclerView(listIntents: MutableList<ResolveInfo>, startMargin: Int, paddingTop:Int,spanCount: Int){

        val csStart = mMainView.getConstraintSet(R.id.start)
        val csEnd = mMainView.getConstraintSet(R.id.end)
        val csFinish = mMainView.getConstraintSet(R.id.finish)


        mRecyclerView = ChooserRecycler(this)
        mRecyclerView.id = R.id.chooserActivityContainer
        mMainView.addView(mRecyclerView)

        mRecyclerView.layoutManager = GridLayoutManager(this, spanCount)
        mRecyclerView.adapter = JJChooserActivityAdapter(this, listIntents,packageManager)
        mRecyclerView.addItemDecoration(ChooserDecoration(JJMargin.bottom(JJScreen.point(100))))
        mRecyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_manager_grid_animation)
        mRecyclerView.setPaddingRelative(JJScreen.point(100),paddingTop,JJScreen.point(100),0)


        JJLayout.mlSetView(mRecyclerView)

        JJLayout.mlSetConstraint(csStart)
        JJLayout.mlBottomToBottomParent(0)
        JJLayout.mlFillParentHorizontally()
        JJLayout.mlTopToTopParent(startMargin)
        JJLayout.mlApply()

        JJLayout.mlSetConstraint(csEnd)
        JJLayout.mlBottomToBottomParent(0)
        JJLayout.mlFillParentHorizontally()
        JJLayout.mlTopToTopParent(JJScreen.percentHeight(0.18f))

        JJLayout.mlSetConstraint(csFinish)
        JJLayout.mlBottomToBottomParent(0)
        JJLayout.mlFillParentHorizontally()
        JJLayout.mlTopToTopParent(JJScreen.percentHeight(0.95f))

        JJLayout.mlDisposeConstraint()
        JJLayout.mlDisposeView()

    }

    private fun setupTitle(text:String?){

        if(text == null) return

        mTittleTextView = AppCompatTextView(this)
        mTittleTextView.id = View.generateViewId()
        mMainView.addView(mTittleTextView)


        val csStart = mMainView.getConstraintSet(R.id.start)
        val csEnd = mMainView.getConstraintSet(R.id.end)
        val csFinish = mMainView.getConstraintSet(R.id.finish)

        mTittleTextView.text = text
        mTittleTextView.setTextColor(Color.parseColor("#262626"))
        mTittleTextView.typeface = Typeface.DEFAULT_BOLD
        mTittleTextView.textSize = JJScreen.responsiveTextSize(15,0,0,14,13).toFloat()
        mTittleTextView.textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
        mTittleTextView.setPaddingRelative(JJScreen.point(100),0,0,JJScreen.point(50))


        JJLayout.mlSetView(mTittleTextView)

        JJLayout.mlSetConstraint(csStart)
        JJLayout.mlTopToTop(mBackgroundIntentsView.id,0)
        JJLayout.mlFillParentHorizontally()
        JJLayout.mlHeight(JJScreen.point(300))

        JJLayout.mlSetConstraint(csEnd)
        JJLayout.mlTopToTop(mBackgroundIntentsView.id,0)
        JJLayout.mlFillParentHorizontally()
        JJLayout.mlHeight(JJScreen.point(300))

        JJLayout.mlSetConstraint(csFinish)
        JJLayout.mlTopToTop(mBackgroundIntentsView.id,0)
        JJLayout.mlFillParentHorizontally()
        JJLayout.mlHeight(JJScreen.point(300))

        JJLayout.mlDisposeConstraint()
        JJLayout.mlDisposeView()

        mTittleTextView.gravity = Gravity.CENTER_VERTICAL

    }

   private fun setupBackground(){
        val csStart = mMainView.getConstraintSet(R.id.start)
        val csEnd = mMainView.getConstraintSet(R.id.end)
        val csFinish = mMainView.getConstraintSet(R.id.finish)

        mBackgroundView = View(this)
        mBackgroundView.id = View.generateViewId()

        mMainView.addView(mBackgroundView)


        mBackgroundView.setBackgroundColor(Color.parseColor("#40000000"))

       JJLayout.mlSetView(mBackgroundView)
       JJLayout.mlSetConstraint(csStart)
       JJLayout.mlFillParent()
       JJLayout.mlApply()

       JJLayout.mlSetConstraint(csEnd)
       JJLayout.mlFillParent()

       JJLayout.mlSetConstraint(csFinish)
       JJLayout.mlFillParent()

       JJLayout.mlDisposeConstraint()
       JJLayout.mlDisposeView()

        mBackgroundView.setOnClickListener {
            mMainView.transitionToState(R.id.finish)

        }
    }

    override fun onIntentLongClicked(resolveInfo: ResolveInfo): Boolean {

        return true
    }

    private var mSecureActivity = true
    private var mIsActivityForResult = false
    override fun onIntentClicked(resolveInfo: ResolveInfo) {
        if(mMapOfIntents.containsKey(resolveInfo.activityInfo.name) && mSecureActivity) {
            mSecureActivity = false
            val intent = Intent(mMapOfIntents[resolveInfo.activityInfo.name])
            intent.component = createComponentName(resolveInfo)

            if (mIsActivityForResult) {
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                startActivity(intent)
            } else startActivity(intent)
        }else    Log.e("JJIntentChooserActivity","ERROR: clicked in ${resolveInfo.activityInfo.name}  missing intent.")

        finish()
    }

    protected fun createComponentName(resolveInfo: ResolveInfo) : ComponentName{
       return ComponentName(
           resolveInfo.activityInfo.packageName,
            resolveInfo.activityInfo.name
        )
    }



    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onBackPressed() {
        mMainView.transitionToState(R.id.finish)
    }

}