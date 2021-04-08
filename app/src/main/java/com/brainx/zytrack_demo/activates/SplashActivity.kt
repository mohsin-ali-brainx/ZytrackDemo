package com.brainx.zytrack_demo.activates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.brainx.androidbase.base.BxBaseSplashActivity
import com.brainx.androidext.ext.showToast
import com.brainx.androidext.ext.startExtActivity
import com.brainx.zytrack_demo.R
import com.brainx.zytrack_demo.api.SharedPreference
import com.brainx.zytrack_demo.databinding.ActivitySplashBinding
import com.brainx.zytrack_demo.datastore.PreferenceDataStore
import com.brainx.zytrack_demo.utils.ZytrackConstant.SPLASH_TIME
import com.brainx.zytrack_demo.utils.setHeaderFromSharedPreference
import com.brainx.zytrack_demo.viewModels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BxBaseSplashActivity<SplashViewModel, ActivitySplashBinding>() {
    //    region private properties
    @Inject
    lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var preferenceDataStore: PreferenceDataStore

    private var isLogin = MutableLiveData<Boolean>(false)

    //    endregion
    //    region Lifecycle methods
    override val mViewModel: SplashViewModel by viewModels()

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun customOnCreate(savedInstanceState: Bundle?) {
        setHeaderFromSharedPreference()
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
    private fun checkUserSession() {
        preferenceDataStore.isLogin.asLiveData().observe(this@SplashActivity, {
            moveToNewScreen(it)
        })
    }

    private fun moveToNewScreen(isLogin: Boolean) {
        if (isLogin) {

        } else {
            startExtActivity(LoginActivity::class.java, isFinish = true)
        }
    }

    //    endregion
}