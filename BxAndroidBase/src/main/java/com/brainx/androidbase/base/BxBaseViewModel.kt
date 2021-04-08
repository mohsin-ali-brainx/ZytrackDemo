package com.brainx.androidbase.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brainx.androidbase.models.LoaderModel


open class BxBaseViewModel : ViewModel() {

    //region properties
    val vmLoadingObserver = MutableLiveData<LoaderModel?>()
    val vmMessageObserver = MutableLiveData<Any?>()

    //endregion

    //region public methods
    fun showProcessingLoader(message: String = "") =
        vmLoadingObserver.postValue(
            LoaderModel(
                true,
                message
            )
        )

    fun hideProcessingLoader() = vmLoadingObserver.postValue(
        LoaderModel(false, "")
    )

    fun showToast(content: Any? = null) = vmMessageObserver.postValue(content)

    //endregion
}