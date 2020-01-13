package com.jjlf.jjkit_intentchooser

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.FloatRange
import kotlin.math.min

class ChooserDrawable : Drawable() {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRect = RectF()
    private var mIsStroke = false
    private var mIsFill = true
    private var mShape = 0
    private var mRadius = floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f)
    private var mPath = Path()
    private var mIsNewPath = false
    private var mSetupPath: ((RectF, Path)-> Unit)? = null
    private var mScaleX = -1f
    private var mScaleY = -1f
    private var mOffsetY = 0f
    private var mOffsetX = 0f
    private var mIsPathClosure = false

    private val mPaintStrokeShadow = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mStrokeShadowRadius = 4f
    private var mIsStrokeShadow = false
    private var mStrokeShadowClosure:((RectF, Path)->Unit)?=null
    private var mPathStrokeShadow = Path()
    private var mRectStrokeShadow = RectF()

    companion object{
        const val CIRCLE = 1
        const val CORNER_SMOOTH_VERY_SAMLL = 2
        const val CORNER_SMOOTH_SAMLL = 3
        const val CORNER_SMOOTH_MEDIUM = 4
        const val CORNER_SMOOTH_LARGE = 5
        const val CORNER_SMOOTH_XLARGE = 6
    }

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.WHITE
        mPaintStroke.style = Paint.Style.STROKE
    }

    fun ssStrokeShadow(@FloatRange(from = 0.0 ,to = 7.0) radius: Float, color: Int, shadowPath: ((RectF, Path)-> Unit)?):ChooserDrawable{
        mStrokeShadowRadius = radius
        mPaintStrokeShadow.style = Paint.Style.STROKE
        mPaintStrokeShadow.strokeWidth = 0.5f
        mPaintStrokeShadow.setShadowLayer(mStrokeShadowRadius,0f,0f,color)
        mStrokeShadowClosure = shadowPath
        mIsStrokeShadow = true
        mIsStroke = false
        return this
    }


    fun ssScale(x: Float,y: Float):ChooserDrawable{
        mScaleX = x
        mScaleY = y
        return this
    }

    fun ssShape(type:Int): ChooserDrawable {
        mIsNewPath = false
        mIsPathClosure = false
        mShape = type
        return this
    }


    fun ssStroke(width: Float, color: Int) : ChooserDrawable{
        mIsStroke = true
        mPaintStroke.strokeWidth = width
        mPaintStroke.color = color
        mIsStrokeShadow = false
        return this
    }

    fun ssStrokeColor(color:Int) : ChooserDrawable {
        mPaintStroke.color = color
        return this
    }


    fun ssStrokeAndShadowLayer(width: Float, color: Int,shadowRadius: Float,shadowOffsetX:Float,shadowOffsetY:Float,shadowColor:Int) : ChooserDrawable{
        mIsStroke = true
        mPaintStroke.strokeWidth = width
        mPaintStroke.color = color
        mPaintStroke.setShadowLayer(shadowRadius,shadowOffsetX,shadowOffsetY,shadowColor)
        mIsStrokeShadow = false
        return this
    }

    fun ssRadius(radius: Float) : ChooserDrawable {
        mRadius = floatArrayOf(radius,radius,radius,radius,radius,radius,radius,radius)
        mIsNewPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun ssRadius(radius: FloatArray) : ChooserDrawable {
        mRadius = radius
        mIsNewPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun ssRadius(topLeft: Float,topRight:Float,bottomRight:Float,bottomLeft:Float) : ChooserDrawable {
        mRadius = floatArrayOf(topLeft,topLeft,topRight,topRight,bottomRight,bottomRight,bottomLeft,bottomLeft)
        mIsNewPath = false
        mIsPathClosure = false
        mShape = 0
        return this
    }

    fun ssFillColor(color: Int) : ChooserDrawable{
        mPaint.color = color
        return this
    }

    fun ssIsFillEnabled(boolean: Boolean) : ChooserDrawable{
        mIsFill = boolean
        return this
    }

    fun ssIsStrokeEnabled(boolean: Boolean) : ChooserDrawable{
        mIsStroke = boolean
        return this
    }

    fun ssIsStrokeShadowEnabled(boolean: Boolean) : ChooserDrawable {
        mIsStrokeShadow = boolean
        return this
    }

    fun ssOffset(x: Float,y: Float):ChooserDrawable{
        mOffsetX = x
        mOffsetY = y
        return this
    }


    fun ssPath(path: Path):ChooserDrawable{
        mIsNewPath = true
        mIsPathClosure = false
        mPath = path
        mShape = 0
        return this
    }

    fun ssPath(closure:(RectF, Path)->Unit):ChooserDrawable{
        mIsNewPath = true
        mIsPathClosure = true
        mSetupPath = closure
        mShape = 0
        return this
    }
    fun diposePath():ChooserDrawable{
        mIsNewPath = false
        mIsPathClosure = false
        mPath.reset()
        mSetupPath = null
        return this
    }

    fun diposeStrokeShadow(): ChooserDrawable {
        mIsStrokeShadow = false
        mPathStrokeShadow.reset()
        mStrokeShadowClosure = null
        return this
    }

    override fun onBoundsChange(bounds: Rect?) {
        if(bounds != null){
            mRect.set(bounds)
            setupRect()
            if(mIsStroke) {
                val inset = mPaintStroke.strokeWidth / 2
                mRect.inset(inset,inset)
            }
        }
    }

    override fun draw(canvas: Canvas) {

        if(mRect.width() > 0f && mRect.height() >0f) {

            if(mIsStrokeShadow) {
                mPathStrokeShadow.reset()
                mRectStrokeShadow.set(mRect)
                mRectStrokeShadow.inset(mStrokeShadowRadius,mStrokeShadowRadius)
                mStrokeShadowClosure?.invoke(mRectStrokeShadow,mPathStrokeShadow)
                mRect.inset(mStrokeShadowRadius+0.25f,mStrokeShadowRadius+0.25f)
                mPaintStrokeShadow.color = if(mIsFill && mPaint.color != 0) mPaint.color else Color.parseColor("#80D1D1D1")
            }


            if(mIsPathClosure  && mIsNewPath){
                mPath.reset()
                mSetupPath?.invoke(mRect, mPath)
            }

            if (!mIsNewPath) {
                mPath.reset()
                mPathStrokeShadow.reset()
                setupRadiusForShape()
                mPath.addRoundRect(mRect, mRadius, Path.Direction.CW)
                mPathStrokeShadow.addRoundRect(mRectStrokeShadow, mRadius, Path.Direction.CW)
            }

            if(mIsStrokeShadow) canvas.drawPath(mPathStrokeShadow, mPaintStrokeShadow)
            if (mIsFill) canvas.drawPath(mPath, mPaint)
            if (mIsStroke) canvas.drawPath(mPath, mPaintStroke)

        }
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        mPaintStroke.alpha = alpha
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaintStroke.colorFilter = colorFilter
        mPaint.colorFilter = colorFilter
        invalidateSelf()
    }


    private fun setupRect(){
        mRect.offset(mOffsetX, mOffsetY)
    }

    private fun setupRadiusForShape(){
        when(mShape){
            1 -> {
                val radius = min(mRect.height(),mRect.width()) / 2f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            2 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.02f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            3 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.05f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            4 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.1f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }

            5 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.15f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }

            6 -> {
                val radius = min(mRect.height(),mRect.width()) * 0.2f
                for (i in 0..7){
                    mRadius[i] = radius
                }
            }
            else -> Log.v("ChooserDrawable","Custom Radius")
        }
    }


}