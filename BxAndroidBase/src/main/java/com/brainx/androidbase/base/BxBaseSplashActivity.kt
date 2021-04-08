package com.brainx.androidbase.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BxBaseSplashActivity<VM : ViewModel, VB : ViewDataBinding> :
    BxBaseActivity<VM, VB>() {

    //region Properties
    private var splashTimeOut = 3000L
    private lateinit var splashTimeOutJob: Job
    //endregion

    //region Abstract Functions
    abstract fun onSplashTimeOut()
    //endregion

    //region Private Methods
    fun startSplashTimeOutJob(timeMs: Long) {
        splashTimeOut = timeMs
        cancelSplashTimeOutJob()
        setupSplashJob()
    }

    fun cancelSplashTimeOutJob() {
        if (::splashTimeOutJob.isInitialized && splashTimeOutJob.isActive) splashTimeOutJob.cancel()
    }

    private fun setupSplashJob() {
        splashTimeOutJob = lifecycleScope.launch {
            delay(splashTimeOut)
            onSplashTimeOut()
        }
    }
    //endregion
}