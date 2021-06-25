package com.brainx.zytrack_demo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.brainx.androidbase.base.BxBaseApp
import com.brainx.androidbase.network.NetworkApi
import com.brainx.zytrack_demo.BuildConfig
import com.brainx.zytrack_demo.R
import java.io.IOException

fun setHeader(headerMap: Map<String, String> = mapOf()) {
    with(ZytrackConstant) {
        NetworkApi.headerMap.apply {
            if (isEmpty()||headerMap.isNullOrEmpty()){
                set(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
                set(APP_PLATFORM_KEY, APP_PLATFORM)
                set(APP_VERSION_KEY, APP_VERSION)
            }else if(!headerMap.isNullOrEmpty()){
                set(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
                set(APP_PLATFORM_KEY, APP_PLATFORM)
                set(APP_VERSION_KEY, APP_VERSION)
                set(CLIENT_KEY, headerMap[CLIENT_KEY] ?: "")
                set(ACCESS_TOKEN_KEY, headerMap[ACCESS_TOKEN_KEY] ?: "")
                set(UID_KEY, headerMap[UID_KEY] ?: "")
            }
        }
    }
}

@SuppressLint("ResourceAsColor")
fun openDialog(
    context: Context,
    title: String = "",
    btn_Text: String,
    message: String,
    isError: Boolean = true,
    response: (Boolean) -> Unit
) {

    val dialog = MaterialDialog(context).apply {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_layout)
        val button = findViewById<TextView>(R.id.okay_button)
        button.text = btn_Text
        findViewById<TextView>(R.id.message_text).text = message
        findViewById<TextView>(R.id.title)?.apply {
            text = title
            if (!isError) {
                setTextColor(R.color.grey_text_color)
            }
        }
        button.setOnClickListener(View.OnClickListener { v: View? ->
            if (this.isShowing) {
                response(true)
                this.dismiss()
            }
        })
    }

    dialog.show()
}

fun openConfirmationDialog(
    context: Context,
    title: String = "",
    message: String,
    response: (Boolean) -> Unit
) {

    val dialog = MaterialDialog(context).apply {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.confirmation_dialog_layout)
        val btn_No = findViewById<TextView>(R.id.no_button)
        val btn_Yes = findViewById<TextView>(R.id.yes_button)
        findViewById<TextView>(R.id.message_text).text = message
        findViewById<TextView>(R.id.title).text = title
        btn_No.setOnClickListener(View.OnClickListener { v: View? ->
            if (this.isShowing) {
                response(false)
                this.dismiss()
            }
        })
        btn_Yes.setOnClickListener(View.OnClickListener { v: View? ->
            if (this.isShowing) {
                response(true)
                this.dismiss()
            }
        })
    }

    dialog.show()
}

fun String?.replaceBrackets():String?{
    var data = this
    if (data?.startsWith("{")==true && data?.endsWith("}")==true){
        data = data?.substring(1, data?.length - 1)
    }
    return data
}

fun String?.toJsonString():String?{
    return "{$this}"
}

fun getPermissionList():List<String>{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}

fun checkLocationPermissions(context: Context): Boolean {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        != PackageManager.PERMISSION_GRANTED
    ) {
        return true
    }
    return false
}

fun checkCameraPermission(context: Context): Boolean {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
        != PackageManager.PERMISSION_GRANTED
    ) {
        return true
    }
    return false
}

fun getCameraPermission():List<String>{
    return listOf(Manifest.permission.CAMERA)
}

@Throws(IOException::class)
fun getBitmap(selectedImg: Uri): Bitmap? {
    val options = BitmapFactory.Options()
    options.inSampleSize = 1
    val fileDescriptor: AssetFileDescriptor? =
        BxBaseApp.getTmContext().contentResolver.openAssetFileDescriptor(selectedImg, "r")
    if (BuildConfig.DEBUG && fileDescriptor == null) {
        error("Assertion failed")
    }
    return BitmapFactory.decodeFileDescriptor(
        fileDescriptor?.fileDescriptor, null, options
    )
}