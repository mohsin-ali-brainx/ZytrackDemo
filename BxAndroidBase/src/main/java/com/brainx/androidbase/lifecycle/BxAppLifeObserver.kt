package com.brainx.androidbase.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
object BxAppLifeObserver : LifecycleObserver {

    var isForeground = MutableLiveData<Boolean>()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
      fun onForeground() {
        isForeground.value = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
     fun onBackground() {
        isForeground.value = false
    }

}