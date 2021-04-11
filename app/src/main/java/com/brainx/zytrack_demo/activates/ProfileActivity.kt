package com.brainx.zytrack_demo.activates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.brainx.androidext.ext.startExtActivity
import com.brainx.zytrack_demo.R
import com.brainx.zytrack_demo.base.BaseActivity
import com.brainx.zytrack_demo.databinding.ActivityLoginBinding
import com.brainx.zytrack_demo.databinding.ActivityProfileBinding
import com.brainx.zytrack_demo.models.UserModel
import com.brainx.zytrack_demo.utils.toJsonString
import com.brainx.zytrack_demo.viewModels.LoginViewModel
import com.brainx.zytrack_demo.viewModels.ProfileViewModel
import com.google.gson.Gson
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
        preferenceDataStore.userData.asLiveData().observe(this,{
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