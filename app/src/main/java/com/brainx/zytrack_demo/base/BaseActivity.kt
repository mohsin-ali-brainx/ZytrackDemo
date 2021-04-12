package com.brainx.zytrack_demo.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.brainx.zytrack_demo.sharedPreference.SharedPreference
import com.brainx.androidext.ext.showToast
import com.brainx.androidbase.base.BxBaseActivity
import com.brainx.androidbase.network.stateManager.NetState
import com.brainx.zytrack_demo.datastore.DataStore
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.brainx.zytrack_demo.utils.openDialog
import com.brainx.zytrack_demo.utils.setHeader
import javax.inject.Inject


abstract class BaseActivity<VM : ViewModel, VB : ViewDataBinding> : BxBaseActivity<VM, VB>() {

    //region Properties
    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var dataStore: DataStore
    private var isUserLoggedIn=false
    //endregion

    //region LifeCycle
    override fun customOnCreate(savedInstanceState: Bundle?) {
        (mViewModel as? BaseViewModel)?.apply {
            errorDialogObserver.observe(this@BaseActivity, this@BaseActivity.errorDialogObserver)
            successDialogObserver.observe(this@BaseActivity, this@BaseActivity.successDialogObserver)
        }
        setHeader()
        dataStore.header.asLiveData().observe(this,headerObserver)
    }

    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        showToast(netState.isSuccess.toString())
    }

    override fun onStart() {
        super.onStart()
        dataStore.header.asLiveData().observe(this,headerObserver)
    }
    //endregion
    // region call backs
    val headerObserver = Observer<Map<String,String>> {
        if (!it.isNullOrEmpty()){
            setHeader(it)
        }
    }

    val errorDialogObserver = Observer<String> {
        if (it.trim().isNotEmpty()) {
            ZytrackConstant.apply {
                openDialog(this@BaseActivity, ERROR, OK, it) {
                }
            }
        }

    }

    val successDialogObserver = Observer<String> {
        if (it.trim().isNotEmpty()) {
            ZytrackConstant.apply {
                openDialog(this@BaseActivity, SUCCESS, OK, it,isError = false) {
                }
            }
        }

    }
    // end region
    // region private methods
    private fun checkUserSession() {
        dataStore.isLogin.asLiveData().observe(this, {
            isUserLoggedIn = it
        })
    }
    // end region
}
