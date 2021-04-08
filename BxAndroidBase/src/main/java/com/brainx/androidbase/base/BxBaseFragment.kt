package com.brainx.androidbase.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.brainx.androidbase.models.LoaderModel
import com.brainx.androidbase.network.stateManager.NetState
import com.brainx.androidbase.network.stateManager.NetworkStateManager

abstract class BxBaseFragment<VM : ViewModel, VB : ViewDataBinding> : Fragment() {

    //region Properties
    protected lateinit var mViewBinding: VB
    //endregion

    //region LifeCycle
    protected abstract val mViewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getViewBinding().apply {
            lifecycleOwner = this@BxBaseFragment
            mViewBinding = this
        }
        customOnViewCreated(savedInstanceState)
        setObservers()
        return mViewBinding.root
    }

    abstract fun getViewBinding(): VB

    abstract fun customOnViewCreated(savedInstanceState: Bundle?)

    //endregion

    //region Open Method
    open fun onNetworkStateChanged(netState: NetState) {}
    //endregion


    //region private method
    private fun setObservers() {
        NetworkStateManager.instance.mNetworkStateCallback.observe(this, Observer {
            onNetworkStateChanged(it)
        })
    }
    //endregion
    // region public methods
    fun getLoadingObserver(): Observer<LoaderModel?> {
        val requiredActivity = requireActivity() as BxBaseActivity<VM,VB>
        return requiredActivity.baseLoadingObserver
    }

    fun getErrorObserver(): Observer<Any?> {
        val requiredActivity = requireActivity() as BxBaseActivity<VM, VB>
        return requiredActivity.baseToastMessageObserver as Observer<Any?>
    }

    // end region
}
