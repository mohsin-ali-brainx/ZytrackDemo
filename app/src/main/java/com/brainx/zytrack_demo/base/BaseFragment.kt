package com.brainx.zytrack_demo.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.brainx.zytrack_demo.sharedPreference.SharedPreference
import com.brainx.androidbase.base.BxBaseFragment
import com.brainx.zytrack_demo.datastore.PreferenceDataStore
import javax.inject.Inject

abstract class BaseFragment<VM : ViewModel, VB : ViewDataBinding> : BxBaseFragment<VM, VB>() {

    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var preferenceDataStore: PreferenceDataStore

    fun getDialogObserver(): Observer<Any?> {
        val requiredActivity = requireActivity() as BaseActivity<VM, VB>
        return requiredActivity.errorDialogObserver as Observer<Any?>
    }

}
