package com.brainx.androidbase.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle

class BxLifeCycleCallBack : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) =
        BxActivityManger.pushActivity(activity)

    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityDestroyed(activity: Activity) = BxActivityManger.popActivity(activity)
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) = Unit
}