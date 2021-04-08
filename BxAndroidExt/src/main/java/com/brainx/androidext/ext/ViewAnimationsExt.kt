package com.brainx.androidext.ext

import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.animation.TranslateAnimation
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.brainx.androidext.extEnums.ExtTranslation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun View.transLateView(
    translationXValue: Float = 0f,
    translationYValue: Float = 0f,
    duration: Long = 1000L
) {
    clearAnimation()
    animate().translationX(translationXValue).translationY(translationYValue).duration = duration
}


fun View.translateViewWithObjectAnimator(
    animateTo: Float,
    propertyName: ExtTranslation,
    duration: Long
) {
    ObjectAnimator.ofFloat(
        this, propertyName.value, animateTo
    ).apply {
        this.duration = duration
        start()
    }
}

fun View.translateViewWithTranslateAnimation(
    propertyName: ExtTranslation,
    from: Float = 0f,
    to: Float,
    duration: Long
) {
    val isHorizontal = propertyName == ExtTranslation.X
    val animation = TranslateAnimation(
        if (isHorizontal) from else 0.0f,
        if (isHorizontal) to else 0.0f,
        if (!isHorizontal) from else 0.0f,
        if (!isHorizontal) to else 0.0f
    )
    animation.apply {
        this.duration = duration
        repeatCount = 1
        fillAfter = false
        startAnimation(this)

    }
}

fun View.changeViewSizeWithAnimation(
    rootOfAnimation: View,
    width: Int? = null,
    height: Int? = null,
    duration: Long? = null,
    onAnimationEnd: (() -> Unit)? = null
) {
    val transitionSet = TransitionSet()
    val changeBounds = ChangeBounds()
    if (duration != null) {
        transitionSet.duration = duration
        changeBounds.duration = duration
    }

    TransitionManager.beginDelayedTransition(
        rootOfAnimation as ViewGroup, transitionSet
            .addTransition(changeBounds)
    )

    val params: ViewGroup.LayoutParams = layoutParams
    width?.apply {
        params.width = width.toInt()
    }
    height?.apply {
        params.height = height.toInt()
    }
    layoutParams = params
    GlobalScope.launch {
        delay(400)
        onAnimationEnd?.invoke()
    }
}


fun View.expandView(
    expandViewHeight: Int,
    speed: Long? = null,
    onExpand: (() -> Unit)?
): List<Int> {
    layoutParams.height = 0
    visibility = View.VISIBLE
    val animation: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            trans: Transformation?
        ) {
            layoutParams.height = (expandViewHeight * interpolatedTime).toInt()
            if (interpolatedTime == 1f) onExpand?.invoke()
            requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    animation.duration =
        speed ?: ((expandViewHeight / context.resources.displayMetrics.density).toLong())
    startAnimation(animation)
    return listOf(expandViewHeight, speed?.toInt()!!)
}

fun View.collapseView(speed: Long? = null, onCollapsed: (() -> Unit)?) {
    if (visibility == View.GONE) return
    val initialHeight = measuredHeight
    val animation: Animation = object : Animation() {
        override fun applyTransformation(
            interpolatedTime: Float,
            t: Transformation?
        ) {
            if (interpolatedTime == 1f) {
                visibility = View.GONE
            } else {
                layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                requestLayout()
            }
            if (interpolatedTime == 1f) onCollapsed?.invoke()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    animation.duration =
        speed ?: (initialHeight / context.resources.displayMetrics.density).toLong()
    startAnimation(animation)
}
