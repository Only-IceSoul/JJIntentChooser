package com.jjlf.jjkit_intentchooser

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.GridLayoutAnimationController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChooserRecycler : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context,attr)

    override fun attachLayoutAnimationParameters(child: View?, params: ViewGroup.LayoutParams?, index: Int, count: Int) {

        if (adapter != null && layoutManager is GridLayoutManager) {

            var animationParams: GridLayoutAnimationController.AnimationParameters? =
                params?.layoutAnimationParameters as? GridLayoutAnimationController.AnimationParameters

            if (animationParams == null) {
                animationParams = GridLayoutAnimationController.AnimationParameters()
                params?.layoutAnimationParameters = animationParams
            }

            val columns = (layoutManager as GridLayoutManager).spanCount


            animationParams.count = count
            animationParams.index = index
            animationParams.columnsCount = columns
            animationParams.rowsCount = count / columns + 1

            animationParams.column = index % columns
            animationParams.row = index % columns + index / columns

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count)
        }
    }
}