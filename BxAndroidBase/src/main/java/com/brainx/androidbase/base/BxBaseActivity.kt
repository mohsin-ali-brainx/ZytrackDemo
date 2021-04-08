package com.brainx.androidbase.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.brainx.androidext.ext.showToast
import com.brainx.androidbase.models.LoaderModel
import com.brainx.androidbase.loaders.ProgressLoader.dismiss
import com.brainx.androidbase.loaders.ProgressLoader.show
import com.brainx.androidbase.network.stateManager.NetState
import com.brainx.androidbase.network.stateManager.NetworkStateManager


abstract class BxBaseActivity<VM : ViewModel, VB : ViewDataBinding> : AppCompatActivity() {

    //region Properties
    protected abstract val mViewModel: VM
    protected lateinit var mViewBinding: VB
    //endregion

    //region lifeCycle method
    abstract fun customOnCreate(savedInstanceState: Bundle?)

    abstract fun getViewBinding(): VB

    open fun onNetworkStateChanged(netState: NetState) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewBinding()
        setContentView(mViewBinding.root)
        mViewBinding.lifecycleOwner = this
        setObservers()
        customOnCreate(savedInstanceState)
    }
    //endregion

    //region private method
    private fun setObservers() {
        (mViewModel as? BxBaseViewModel)?.apply {
            vmLoadingObserver.observe(this@BxBaseActivity, baseLoadingObserver)
            vmMessageObserver.observe(this@BxBaseActivity, baseToastMessageObserver)

        }
        NetworkStateManager.instance.mNetworkStateCallback.observe(this, Observer {
            onNetworkStateChanged(it)
        })
    }
    //endregion

    //region Public/Protected Method
    fun showLoading(
        title: String? = null,
        isCancellable: Boolean = false,
        timeOut: Long? = null
    ) {
        if (isFinishing) return
        show(this, title, isCancellable = isCancellable, timeOut = timeOut)
    }

    fun dismissLoading() {
        dismiss()
    }

    fun showToastMessage(message: Any?) {
        showToast(message)
    }
    //endregion

    //region CallBack
     val baseLoadingObserver = Observer<LoaderModel?> { loading ->
        if (loading?.status == true) showLoading(loading?.message)
        else dismissLoading()
    }

   val baseToastMessageObserver = Observer<Any?> {
        showToast(it)
    }

    //endregion
}
