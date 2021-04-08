package com.brainx.androidext.ext

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.TypedValue
import androidx.core.content.ContextCompat

fun Context.getAccentColor(): Int {
    val attr =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) android.R.attr.colorAccent
        else resources.getIdentifier("colorAccent", "attr", packageName)
    val outValue = TypedValue()
    theme.resolveAttribute(attr, outValue, true)
    return outValue.data
}

fun Int.lighten(fraction: Double): Int {
    var red = Color.red(this)
    var green = Color.green(this)
    var blue = Color.blue(this)
    red = Math.min(red + red * fraction, 255.0).toInt()
    green = Math.min(green + green * fraction, 255.0).toInt()
    blue = Math.min(blue + blue * fraction, 255.0).toInt()
    val alpha = Color.alpha(this)
    return Color.argb(alpha, red, green, blue)
}

fun Int.darken(fraction: Double): Int {
    var red = Color.red(this)
    var green = Color.green(this)
    var blue = Color.blue(this)
    red = Math.max(red - red * fraction, 0.0).toInt()
    green = Math.max(green - green * fraction, 0.0).toInt()
    blue = Math.max(blue - blue * fraction, 0.0).toInt()
    val alpha = Color.alpha(this)
    return Color.argb(alpha, red, green, blue)
}

// Zytrack Extension region
fun  Int.getFilterColor(context: Context): ColorFilter {
    return PorterDuffColorFilter(
        ContextCompat.getColor(
            context,
            this
        ), PorterDuff.Mode.SRC_ATOP
    )
}
//end region
