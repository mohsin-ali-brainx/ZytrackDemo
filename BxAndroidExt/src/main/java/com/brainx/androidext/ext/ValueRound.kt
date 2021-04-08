package com.brainx.androidext.ext


fun Float.roundToFloat(toNumber: Int = 2): Float {

    return String.format("%.${toNumber}f", this).toFloat()
}

fun Double.roundToDouble(toNumber: Int = 2): Double {
    return String.format("%.${toNumber}f", this).toDouble()
}

fun Int.roundToString(toNumber: Int = 2): String {
    return String.format("%0${toNumber}d", this)
}
