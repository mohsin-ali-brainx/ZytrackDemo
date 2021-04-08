package com.brainx.androidext.ext

import android.content.Context

fun Any.getDpFromPx(context: Context): Float {
    return when (this) {
        is Int -> (this / context.resources.displayMetrics.density)
        is Float -> (this / context.resources.displayMetrics.density)
        is Double -> (this / context.resources.displayMetrics.density).toFloat()
        else -> 0f
    }
}

fun Any.getPxFromDp(context: Context): Float {
    return when (this) {
        is Int -> (this * context.resources.displayMetrics.density)
        is Float -> (this * context.resources.displayMetrics.density)
        is Double -> (this * context.resources.displayMetrics.density).toFloat()
        else -> 0f
    }
}

fun Int.getDp(context: Context): Int =
    (this * context.resources.displayMetrics.density + 0.5f).toInt()


fun Int.millimeterToCM(): Double = (this / 10.0)

fun Double.cmToMillimeter(roundToNumber: Int = 1): Double =
    (this * 10.0).roundToDouble(roundToNumber)

fun Int.millimeterToInch(roundToNumber: Int = 1): Double =
    (this / 25.4).roundToDouble(roundToNumber)

fun Double.inchToMillimeter(roundToNumber: Int = 1): Double =
    (this * 25.4).roundToDouble(roundToNumber)
