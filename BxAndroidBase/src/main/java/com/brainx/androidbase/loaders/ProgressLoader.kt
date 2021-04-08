package com.brainx.androidbase.loaders

import android.app.Activity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


object ProgressLoader : CoroutineScope {

    private var mProgressDialogLib: KProgressHUD? = null
    private var autoDismissJob: Job? = null
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + Job()

    fun isShowing(): Boolean = mProgressDialogLib?.isShowing ?: false

    fun dismiss() {
        autoDismissJob?.cancel()
        mProgressDialogLib?.dismiss()
    }

    fun show(
        activity: Activity,
        title: String? = null,
        timeOut: Long? = null,
        isCancellable: Boolean = false
    ) {
        if (activity.isFinishing) return
        dismiss()
        KProgressHUD.create(activity).apply {
            setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            // setBackgroundColor(ContextCompat.getColor(activity, R.color.colorProgressBg))
            setCancellable(isCancellable)
            setAnimationSpeed(2)
            setDimAmount(0.5f)
            if (!title.isNullOrEmpty())
                setLabel(title)
            show()
            mProgressDialogLib = this
        }
        starAutoDismissJob(timeOut)
    }


    private fun starAutoDismissJob(waitInMs: Long?) {
        autoDismissJob?.cancel()
        if (waitInMs == null) return
        autoDismissJob = launch {
            delay(waitInMs)
            dismiss()
        }
    }
}