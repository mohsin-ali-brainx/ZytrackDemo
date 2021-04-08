package com.brainx.androidext.ext

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File

fun ImageView.setImageDrawableWithTint(drawableId: Int, colorID: Int?) {
    if (colorID == null) return
    val drawable = ContextCompat.getDrawable(context, drawableId)
    drawable?.setDrawableTint(context, ContextCompat.getColor(context, colorID))
    setImageDrawable(drawable)
}

fun ImageView.setImageDrawableWithTint(drawableId: Int, colorCode: String?) {
    if (colorCode == null) return
    val drawable = ContextCompat.getDrawable(context, drawableId)
    drawable?.setDrawableTint(colorCode)
    setImageDrawable(drawable)
}

@SuppressLint("CheckResult")
fun ImageView.loadImage(
    image: Any?,
    placeHolder: Int? = null,
    errorImage: Int? = null,
    loadingProgress: ProgressBar? = null,
    isCaching: Boolean = true,
    listener: RequestListener<Drawable>? = null
) {
    try {
        val nullAt = "null"
        loadingProgress?.visibility = View.VISIBLE
        val reqManager = Glide.with(context)

        val requestCreator = when (image) {
            is String -> {
                if (URLUtil.isValidUrl(image))
                    reqManager.load(if (image.isNotEmpty()) image else nullAt)
                else {
                    reqManager.load(File(if (image.isNotEmpty()) image else nullAt))
                }
            }
            is Int -> reqManager.load(image)
            is File -> reqManager.load(image)
            is Uri -> reqManager.load(image)
            is Drawable -> reqManager.load(image)
            else -> reqManager.load(nullAt)
        }
        if (!isCaching) {
            requestCreator.diskCacheStrategy(DiskCacheStrategy.NONE)
            requestCreator.skipMemoryCache(true)
        }

        if (placeHolder != null)
            requestCreator.placeholder(placeHolder)
        if (errorImage != null)
            requestCreator.error(errorImage)

        requestCreator.listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                loadingProgress?.visibility = View.GONE
                listener?.onLoadFailed(e, model, target, isFirstResource)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                loadingProgress?.visibility = View.GONE
                listener?.onResourceReady(resource, model, target, dataSource, isFirstResource)
                return false
            }
        })
        requestCreator.into(this)
    } catch (ex: Exception) {
    }

}
