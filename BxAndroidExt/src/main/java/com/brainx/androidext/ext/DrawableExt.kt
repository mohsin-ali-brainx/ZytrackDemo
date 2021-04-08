package com.brainx.androidext.ext

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

fun Drawable.setDrawableTint(context: Context, color: Int?) {
    if (color == null) return
    val wrappedDrawable = DrawableCompat.wrap(this)
    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, color))
}

fun Drawable.setDrawableTint(colorCode: String?) {
    if (colorCode == null) return
    val wrappedDrawable = DrawableCompat.wrap(this)
    DrawableCompat.setTint(wrappedDrawable, Color.parseColor(colorCode))
}
