package com.brainx.zytrack_demo.activates

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.brainx.androidbase.base.BxBaseSplashActivity
import com.brainx.androidext.ext.startExtActivity
import com.brainx.zytrack_demo.sharedPreference.SharedPreference
import com.brainx.zytrack_demo.databinding.ActivitySplashBinding
import com.brainx.zytrack_demo.datastore.PreferenceDataStore
import com.brainx.zytrack_demo.utils.ZytrackConstant.SPLASH_TIME
import com.brainx.zytrack_demo.utils.setHeader
import com.brainx.zytrack_demo.viewModels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BxBaseSplashActivity<SplashViewModel, ActivitySplashBinding>() {
    //    region private properties
    @Inject
    lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var preferenceDataStore: PreferenceDataStore

    private var isUserLoggedIn = false

    private var isLogin = MutableLiveData<Boolean>(false)

    //    endregion
    //    region Lifecycle methods
    override val mViewModel: SplashViewModel by viewModels()

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun customOnCreate(savedInstanceState: Bundle?) {
        setHeader()
        preferenceDataStore.header.asLiveData().observe(this, headerObserver)
        startSplashTimeOutJob(SPLASH_TIME)
    }

    override fun onSplashTimeOut() {
        checkUserSession()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    //    endregion
    //    region private methods
    val headerObserver = Observer<Map<String,String>> {
        if (!it.isNullOrEmpty()){
            setHeader(it)
        }
    }

    private fun checkUserSession() {
        preferenceDataStore.isLogin.asLiveData().observe(this@SplashActivity, {
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

    //    endregion
}