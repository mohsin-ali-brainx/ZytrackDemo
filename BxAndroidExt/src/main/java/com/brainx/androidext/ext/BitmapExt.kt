package com.brainx.androidext.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View


fun View.toBitmap(): Bitmap? {
    if (width == 0 || height == 0) return null
    val returnedBitmap: Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(returnedBitmap)
    val bgDrawable: Drawable? = background
    if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.TRANSPARENT)
    draw(canvas)
    return returnedBitmap
}