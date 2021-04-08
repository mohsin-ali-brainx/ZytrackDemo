package com.brainx.androidext.ext


import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment


fun <T> Activity.startExtActivity(
    activityClass: Class<T>,
    activityOption: ActivityOptions? = null,
    isFinishAffinity: Boolean = false,
    isFinish: Boolean = false,
    intent: Intent? = null,
    bundle: Bundle? = null,
) {
    val newIntent = intent ?: Intent(this@startExtActivity, activityClass)
    bundle?.apply { newIntent.putExtras(bundle) }

    activityOption?.apply {
        startActivity(newIntent, toBundle())
    } ?: run {
        startActivity(newIntent)
    }
    if (isFinishAffinity) finishAffinity()
    if (isFinish && !isFinishAffinity) finish()
}

fun <T> Activity.startExtActivityOnResult(
    activityClass: Class<T>,
    activityOption: ActivityOptions? = null,
    intent: Intent? = null,
    bundle: Bundle? = null,
    resultCode:Int
) {
    val newIntent = intent ?: Intent(this@startExtActivityOnResult, activityClass)
    bundle?.apply { newIntent.putExtras(bundle) }

    activityOption?.apply {
        startActivityForResult(newIntent,resultCode,toBundle())
    } ?: run {
        startActivityForResult(newIntent,resultCode)
    }

}

fun <T> Fragment.startExtActivityOnResult(
    activityClass: Class<T>,
    activityOption: ActivityOptions? = null,
    intent: Intent? = null,
    bundle: Bundle? = null,
    resultCode:Int
) {
    val newIntent = intent ?: Intent(activity, activityClass)
    bundle?.apply { newIntent.putExtras(bundle) }

    activityOption?.apply {
        startActivityForResult(newIntent,resultCode,toBundle())
    } ?: run {
        startActivityForResult(newIntent,resultCode)
    }

}



