package com.brainx.zytrack_demo.activates

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.brainx.androidbase.base.BxBaseSplashActivity
import com.brainx.androidext.ext.startExtActivity
import com.brainx.zytrack_demo.sharedPreference.SharedPreference
import com.brainx.zytrack_demo.databinding.ActivitySplashBinding
import com.brainx.zytrack_demo.datastore.DataStore
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.brainx.zytrack_demo.utils.ZytrackConstant.SPLASH_TIME
import com.brainx.zytrack_demo.utils.openConfirmationDialog
import com.brainx.zytrack_demo.utils.openDialog
import com.brainx.zytrack_demo.utils.setHeader
import com.brainx.zytrack_demo.viewModels.SplashViewModel
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BxBaseSplashActivity<SplashViewModel, ActivitySplashBinding>() {
    //    region private properties
    @Inject
    lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var dataStore: DataStore

    private var isUserLoggedIn = false
    private var  appUpdateManager: AppUpdateManager?=null

    private val APP_UPDATE_REQUEST_CODE = 10001

    //    endregion
    //    region Lifecycle methods
    override val mViewModel: SplashViewModel by viewModels()

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun customOnCreate(savedInstanceState: Bundle?) {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        registerUpdateListener(true) // only use register Update Listener when update type is Flexible
        checkFlexibleUpdate()
        setHeader()
        dataStore.header.asLiveData().observe(this, headerObserver)
        startSplashTimeOutJob(SPLASH_TIME)
    }

    override fun onSplashTimeOut() {
        checkUserSession()
    }

    override fun onStop() {
        registerUpdateListener(false) // only use register Update Listener when update type is Flexible
        super.onStop()
    }
    //    endregion
    //    region private methods
    private val headerObserver = Observer<Map<String,String>> {
        if (!it.isNullOrEmpty()){
            setHeader(it)
        }
    }

    private val appUpdateListener: InstallStateUpdatedListener? = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            openDialog(this, ZytrackConstant.SUCCESS, ZytrackConstant.OK, "New App is ready",isError = false) {
                if (it) appUpdateManager?.completeUpdate()
            }
        }
    }

    private fun registerUpdateListener(register:Boolean){
        appUpdateManager?.apply {
            appUpdateListener?.let {
                when(register){
                    true->  registerListener(it)
                    false-> unregisterListener(it)
                }
            }
        }
    }

    private fun checkUserSession() {
        dataStore.isLogin.asLiveData().observe(this@SplashActivity, {
            isUserLoggedIn = it
            moveToNewScreen(it)
        })
    }

    private fun moveToNewScreen(isLogin: Boolean) {
        if (isLogin) {
            startExtActivity(ProfileActivity::class.java, isFinish = true)
        } else {
            startExtActivity(LoginActivity::class.java, isFinish = true)
        }
    }

    private fun checkFlexibleUpdate() {
        // Returns an intent object that you use to check for an update.
        // Checks that the platform will allow the specified type of update.
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        // Request the update.
                        appUpdateManager?.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.FLEXIBLE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            APP_UPDATE_REQUEST_CODE)
                    }catch (exception:IntentSender.SendIntentException){

                    }
            }
        }
    }

    private fun checkImmediateUpdate(onCreate:Boolean) {
        when(onCreate){
            true->{
                // Returns an intent object that you use to check for an update.
                // Checks that the platform will allow the specified type of update.
                appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                            try {
                                // Request the update.
                                appUpdateManager?.startUpdateFlowForResult(
                                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                    appUpdateInfo,
                                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                    AppUpdateType.IMMEDIATE,
                                    // The current activity making the update request.
                                    this,
                                    // Include a request code to later monitor this update request.
                                    APP_UPDATE_REQUEST_CODE)
                            }catch (exception:IntentSender.SendIntentException){

                            }
                    }
                }
            }
            false->{
                appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        try {
                            // Request the update.
                            appUpdateManager?.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                this,
                                // Include a request code to later monitor this update request.
                                APP_UPDATE_REQUEST_CODE)
                        }catch (exception:IntentSender.SendIntentException){

                        }
                    }
                }
            }
        }
    }
    //    endregion
    // region implemented methods
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==APP_UPDATE_REQUEST_CODE){
            when(resultCode){
                RESULT_CANCELED->{
                    openConfirmationDialog(this,"App Update","App Cancelled"){}
                }
                RESULT_OK->{

                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED->{
                    checkFlexibleUpdate()
                }
            }
        }
    }
    //    endregion

}