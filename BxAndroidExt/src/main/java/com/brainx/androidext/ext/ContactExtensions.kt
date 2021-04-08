package com.brainx.androidext.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun Context.openDialPad(number: String) {
    try {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$number")
        startActivity(callIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
    }
}