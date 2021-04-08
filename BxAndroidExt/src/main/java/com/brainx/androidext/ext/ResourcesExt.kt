package com.brainx.androidext.ext

import android.content.Context
import android.util.TypedValue

fun Context.getFloatResources(id: Int): Float {
    val typedValue = TypedValue()
    resources.getValue(id, typedValue, true)
    return typedValue.float
}