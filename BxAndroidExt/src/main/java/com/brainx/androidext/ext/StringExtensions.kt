package com.brainx.androidext.ext

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.brainx.androidext.utils.ExtRegexUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

fun String.isValidEmail(): Boolean = isValid(ExtRegexUtils.EMAIL_REGEX)
fun String.isValidPassword(): Boolean = isValid(ExtRegexUtils.PASSWORD_REGEX)


fun String.isValid(regex: String?): Boolean {
    if (regex.isNullOrEmpty()) return false
    val matcher = Pattern.compile(regex).matcher(this)
    return matcher.matches()
}

fun String.trimExtraSpaces(): String = replace("\\s+".toRegex(), " ").trim()

fun String.capFirstLetter(): String {
    if (isNullOrEmpty()) return ""
    return substring(0, 1)
        .toUpperCase(Locale.ROOT) + substring(1)
}

fun String.capWordFirstLatter(): String = this.split(' ')
    .joinToString(" ") { it.capitalize(Locale.getDefault()) }

//Zytrack Extensions
fun String?.dialogOK(
    context: Context, btnText: String?, isFinish: Boolean,btn_Color:Int,
    callback: (Boolean) -> Unit
) {

    val alertDialogBuilder = AlertDialog.Builder(context)
    alertDialogBuilder.setTitle("")
    alertDialogBuilder.setMessage(this)

    alertDialogBuilder.setCancelable(false)

    alertDialogBuilder.setPositiveButton(
        btnText
    ) { dialog, which ->
        if (isFinish)
            (context as Activity).finish()
        callback(true)
        dialog.dismiss()
    }
    val alertDialog = alertDialogBuilder.create()
    alertDialog.show()
    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
        ContextCompat.getColor(
            context,
            btn_Color
        )
    )
}

fun String.parseFormatDdMmYyyyy() =
    SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(this)

fun String.capWordFirstLetterWithoutUnderscore(): String = this.split('_')
    .joinToString(" ") { it.capitalize(Locale.getDefault()) }

//end region