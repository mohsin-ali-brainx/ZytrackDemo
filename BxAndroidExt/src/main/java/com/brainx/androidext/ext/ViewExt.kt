package com.brainx.androidext.ext

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout


fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) beInvisible() else beVisible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun View.beInvisible() {
    visibility = View.INVISIBLE
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

fun View.beVisibleOrGone(stats: Boolean) {
    visibility = if (stats) View.VISIBLE else View.GONE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE


fun ViewGroup.views(): List<View> = (0 until childCount).map { getChildAt(it) }

fun View.setViewSize(width: Int = -1, height: Int = -1) {
    val params = layoutParams
    if (width >= 0)
        params.width = width
    if (height >= 0)
        params.height = height
    layoutParams = params
    Handler().post { requestLayout() }
}

fun View.setViewMargin(
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
    bottom: Int? = null
) {

    when (this.parent) {
        is LinearLayout -> {
            val params = layoutParams as LinearLayout.LayoutParams
            params.setMargins(
                left ?: params.leftMargin,
                top ?: params.topMargin,
                right ?: params.rightMargin,
                bottom ?: params.bottomMargin
            )
            layoutParams = params
        }
        is RelativeLayout -> {
            val params = layoutParams as RelativeLayout.LayoutParams
            params.setMargins(
                left ?: params.leftMargin,
                top ?: params.topMargin,
                right ?: params.rightMargin,
                bottom ?: params.bottomMargin
            )
            layoutParams = params
        }
        is ConstraintLayout -> {
            val params = layoutParams as ConstraintLayout.LayoutParams
            params.setMargins(
                left ?: params.leftMargin,
                top ?: params.topMargin,
                right ?: params.rightMargin,
                bottom ?: params.bottomMargin
            )
            layoutParams = params
        }
        is FrameLayout -> {
            val params = layoutParams as FrameLayout.LayoutParams
            params.setMargins(
                left ?: params.leftMargin,
                top ?: params.topMargin,
                right ?: params.rightMargin,
                bottom ?: params.bottomMargin
            )
            layoutParams = params
        }
    }

}


fun View.setOnGlobalLayoutChangeListener(
    isConsiderPrevious: Boolean = true,
    isGreaterThenZero: Boolean = false,
    action: () -> Unit

) {
    if ((width > 0 || height > 0) && isConsiderPrevious) {
        action()
        return
    }
    viewTreeObserver.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (width < 0 || height < 0 || (isGreaterThenZero && (width == 0 || height == 0))) return
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action()
        }
    })
}

