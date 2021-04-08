package com.brainx.zytrack_demo.activates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.brainx.zytrack_demo.R
import com.brainx.zytrack_demo.base.BaseActivity
import com.brainx.zytrack_demo.databinding.ActivityLoginBinding
import com.brainx.zytrack_demo.databinding.ActivityProfileBinding
import com.brainx.zytrack_demo.viewModels.LoginViewModel
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
        setBinding()
        readUserData()
    }

    private fun setBinding(){
        mViewBinding.apply {
            viewModel = mViewModel
        }
    }

    private fun readUserData(){
        preferenceDataStore.userData.asLiveData().observe(this,{
            it?.let {
                mViewModel.user.set(it)
            }
        })
    }
    //    end region
}