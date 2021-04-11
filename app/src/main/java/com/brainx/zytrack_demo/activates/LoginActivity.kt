package com.brainx.zytrack_demo.activates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.brainx.androidext.ext.hideKeyboard
import com.brainx.androidext.ext.onDone
import com.brainx.androidext.ext.startExtActivity
import com.brainx.zytrack_demo.R
import com.brainx.zytrack_demo.base.BaseActivity
import com.brainx.zytrack_demo.databinding.ActivityLoginBinding
import com.brainx.zytrack_demo.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {
    //    region private properties
    override val mViewModel: LoginViewModel by viewModels()
    //    endregion
    // lifecycle region
    override fun getViewBinding() = ActivityLoginBinding.inflate(layoutInflater)

    override fun customOnCreate(savedInstanceState: Bundle?) {
        super.customOnCreate(savedInstanceState)
        init()
    }
    //    end region
    //region private Methods
    private fun init() {
        mViewModel.activityContext = this
        showTimeout()
        setBinding()
        onEnterKeyPressed()
        isUserLoggedInObservable()
    }
    private fun showTimeout() {
        if (intent.extras?.getBoolean(getString(R.string.session_timeout)) == true) {
            mViewModel.showErrorDialog(getString(R.string.session_timeout))
        }
    }
    private fun setBinding(){
        mViewBinding.apply {
            viewModel = mViewModel
            clickListener = this@LoginActivity
            mViewModel.showPassword(false, edtPassword)
        }
    }

    private fun isUserLoggedInObservable() {
        mViewModel.isUserLoggedIn.observe(this, {
            if (it) {
                startExtActivity(ProfileActivity::class.java, isFinish = true)
            }
        })
    }

    private fun onEnterKeyPressed(){
        mViewBinding.apply {
            edtPassword.onDone {
                loginClicked()
            }
        }
    }

    private fun loginClicked(){
        hideKeyboard()
        mViewModel.onLoginClicked(mViewBinding.cbRememberMe.isChecked)
    }

    //    endregion

    //region public Methods
    fun onLoginClicked(v: View) {
        loginClicked()
    }

    //    endregion
}