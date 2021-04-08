package com.brainx.androidbase.lifecycle

import android.app.Activity
import java.util.*

object BxActivityManger {

    private val mActivityList = LinkedList<Activity>()

    val currentActivity: Activity? get() = if (mActivityList.isEmpty()) null else mActivityList.last

    fun pushActivity(activity: Activity) {
        mActivityList.apply {
            if (contains(activity)) {
                if (last == activity) return
                remove(activity)
                add(activity)
            } else {
                add(activity)
            }
        }
    }

    fun popActivity(activity: Activity) {
        mActivityList.remove(activity)
    }

    fun finishCurrentActivity() {
        currentActivity?.finish()
    }

    fun finishActivity(activity: Activity) {
        mActivityList.remove(activity)
        activity.finish()
    }

    fun finishActivity(clazz: Class<*>) {
        for (activity in mActivityList)
            if (activity.javaClass == clazz)
                activity.finish()
    }

    fun finishAllActivity() {
        mActivityList.forEach(Activity::finish)
    }
}