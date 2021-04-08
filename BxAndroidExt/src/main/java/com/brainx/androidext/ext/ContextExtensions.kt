package com.brainx.androidext.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.*

fun Context.runTimePermissions(permissions: List<String>,
                               permissionResponse: (Boolean, Boolean) -> Unit
) {
    Dexter.withContext(this)
        .withPermissions(permissions)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {

                    if (report.areAllPermissionsGranted()) {
                        permissionResponse(true, false)
                        return
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        permissionResponse(false, true)
                        return
                    }

                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                token?.continuePermissionRequest()
                permissionResponse(false, false)
            }
        })
        .withErrorListener {
        }
        .check()
}

fun Context.openSettings(){
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}


