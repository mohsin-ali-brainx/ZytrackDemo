package com.brainx.zytrack_demo.base

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.brainx.androidbase.base.BxBaseViewModel
import com.brainx.androidbase.models.AppException
import com.brainx.androidbase.network.model.Error
import com.brainx.zytrack_demo.R

open class BaseViewModel : BxBaseViewModel(){
    //region properties
    val errorDialogObserver = MutableLiveData<String>()
    val successDialogObserver = MutableLiveData<String>()
//    endregion

    //    region public methods
    fun showErrorDialog(content: String) = errorDialogObserver.postValue(content)
    fun showSuccessDialog(content: String) = successDialogObserver.postValue(content)

    fun showError(error: AppException, context: Activity?){
        error?.apply {
            if (errCode== Error.INTERNET_UNAVAILABLE.getKey()){
                context?.resources?.getString(R.string.no_internet)?.let {
                    showErrorDialog(
                        it
                    )
                }
            }else{
                showErrorDialog(errorMsg)
            }
        }
    }
    // end region
}