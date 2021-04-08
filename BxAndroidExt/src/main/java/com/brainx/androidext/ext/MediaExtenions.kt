package com.brainx.androidext.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


fun File.getFileExtension(): String? {
    return Uri.fromFile(this)?.lastPathSegment?.split(".")?.get(1)
}

fun File.getUriFromFile(context: Context, authority: String): Uri {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Uri.fromFile(this)
    } else {
        FileProvider.getUriForFile(context, authority, this)
    }
}

fun createDir(path: String, dirName: String): String {
    val userDir = File("${path}/${dirName}")
    if (!userDir.isDirectory) {
        userDir.mkdirs()
    }
    return userDir.absolutePath
}

fun createFile(name: String, path: String): File {
    val file = File(path, name)
    file.createNewFile()
    return file
}


fun Context.createTempFile(prefix: String, suffix: String, shouldOverride: Boolean): File {
    return File.createTempFile(
        prefix + if (!shouldOverride) UUID.randomUUID() else "", suffix, cacheDir
    )
}

fun Any.imageToBase64(quality: Int = 100): String? {
    val filePath = when (this) {
        is File -> path
        is String -> this
        else -> return null
    }
    val bitmap = BitmapFactory.decodeFile(filePath)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}


fun Any.imageToByteArray(quality: Int = 100): ByteArray? {
    val filePath = when (this) {
        is File -> path
        is String -> this
        else -> return null
    }
    val bitmap = BitmapFactory.decodeFile(filePath)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return outputStream.toByteArray()
}

fun convertToBase64(attachment: File): String {
    return Base64.encodeToString(attachment.readBytes(), Base64.NO_WRAP)
}

