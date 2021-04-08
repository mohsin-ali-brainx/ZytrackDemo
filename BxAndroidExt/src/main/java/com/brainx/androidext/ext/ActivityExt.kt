package com.brainx.androidext.ext

import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat


fun Activity.getScreenSize(): List<Int>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets: Insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        val width = windowMetrics.bounds.width() - insets.left - insets.right
        val height = windowMetrics.bounds.width() - insets.top - insets.bottom
        listOf(width, height)

    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        listOf(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}


fun Activity.statusBarColor(id: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, id)
    }
}

fun Activity.showKeyboard(view: View?, delay: Long = 0) {

    if (view == null) return
    val handler = Handler(Looper.getMainLooper())
    val runnable = Runnable {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.showSoftInput(view, 0)
    }
    handler.postDelayed(runnable, delay)
}

fun Activity.hideKeyboard() {
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    this.currentFocus?.let { v ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}


fun Activity.showToast(message: Any?) {
    val messageString = when (message) {
        is String -> message
        is Int -> getString(message)
        else -> null
    }
    if (messageString.isNullOrEmpty()) return
    try {
        Toast.makeText(this, messageString, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
    }
}


// Zytrack Extension region
fun Activity.setSystemUiLightStatusBar(isLightStatusBar: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val systemUiAppearance = if (isLightStatusBar) {
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            } else {
                0
            }
            window.insetsController?.setSystemBarsAppearance(systemUiAppearance,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            val systemUiVisibilityFlags = if (isLightStatusBar) {
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            window.decorView.systemUiVisibility = systemUiVisibilityFlags
        }
    }
}

// end region