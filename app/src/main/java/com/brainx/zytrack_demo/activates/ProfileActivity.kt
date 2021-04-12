package com.brainx.zytrack_demo.activates

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.brainx.androidext.ext.startExtActivity
import com.brainx.zytrack_demo.base.BaseActivity
import com.brainx.zytrack_demo.databinding.ActivityProfileBinding
import com.brainx.zytrack_demo.viewModels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ProfileViewModel, ActivityProfileBinding>() {
    //    region private properties
    override val mViewModel: ProfileViewModel by viewModels()
    //    endregion
    // lifecycle region
    override fun getViewBinding() = ActivityProfileBinding.inflate(layoutInflater)

    override fun customOnCreate(savedInstanceState: Bundle?) {
        super.customOnCreate(savedInstanceState)
        init()
    }
    //    end region
    //region private Methods
    private fun init() {
        mViewModel.requireActivity = this
        setBinding()
        readUserData()
        logoutObserver()
    }

    private fun setBinding(){
        mViewBinding.apply {
            viewModel = mViewModel
        }
    }

    private fun readUserData(){
        dataStore.user.asLiveData().observe(this,{
            mViewModel.user.set(it)
        })
    }

    private fun logoutObserver(){
        mViewModel.logOutObserver.observe(this,{
            if (it){
                startExtActivity(LoginActivity::class.java,isFinishAffinity = true)
            }
        })
    }
    //    end region
    // region public methods
    // end region
}