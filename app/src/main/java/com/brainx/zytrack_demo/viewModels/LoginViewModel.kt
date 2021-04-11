package com.brainx.zytrack_demo.viewModels

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.databinding.ObservableBoolean
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brainx.androidbase.models.AppException
import com.brainx.androidext.ext.enablePasswordVisibility
import com.brainx.androidext.ext.isValidEmail
import com.brainx.androidext.ext.toJson
import com.brainx.zytrack_demo.sharedPreference.SharedPreference
import com.brainx.zytrack_demo.base.BaseViewModel
import com.brainx.zytrack_demo.datastore.PreferenceDataStore
import com.brainx.zytrack_demo.models.UserModel
import com.brainx.zytrack_demo.repository.AuthRepository
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val sharedPreference: SharedPreference,
    private val authRepository: AuthRepository,
    private val preferenceDataStore: PreferenceDataStore,
) : BaseViewModel() {
    //region public properties
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var showPassword: ObservableBoolean = ObservableBoolean(false)
    var isAccountBlocked = MutableLiveData<Boolean>(false)
    var isUserLoggedIn = MutableLiveData<Boolean>(false)
    var activityContext: Activity?=null
    // end region

    //region public method
    fun togglePasswordVisibilty(v: View, editText: EditText) {
        when (showPassword.get()) {
            true -> {
                showPassword(false, editText)
            }
            false -> {
                showPassword(true, editText)
            }
        }
    }

    fun showPassword(show: Boolean, editText: EditText) {
        showPassword.set(show)
        editText.enablePasswordVisibility(show)
    }

    fun onLoginClicked(rememberMe: Boolean) {
        if (!isValid()) return
        signInApi()
    }
    //end region

    //region private method
    private fun signInApi() {
        var isLogin:Boolean=false
        showProcessingLoader()
        viewModelScope.launch(Dispatchers.IO) {
            ZytrackConstant.apply {
                val user = UserModel(
                    email = email.value,
                    password = password.value,
                    device_token = DEVICE_TOKEN_VALUE
                )
                authRepository.signIn(
                    user, { userModel, status , token, client,uid->
                        if (status) {
                            isLogin=status
                            setUserDetails(userModel, isLogin, token, client, uid)
                        }
                    }, {
                        hideProcessingLoader()
                        showErrorMessage(it)
                    }
                )
            }

        }
    }

    private fun setUserDetails(
        user:UserModel?,
        isLogin:Boolean,
        token:String?,
        client:String?,
        uid:String?){
        viewModelScope.launch(Dispatchers.IO) {
            preferenceDataStore.apply {
                token?.let {token->
                    client?.let { client->
                        uid?.let { uid->
                            headers(token,client,uid)
                        }
                    }
                }
                isLogin(isLogin,{
                    if (it){
                        this@LoginViewModel.isUserLoggedIn.postValue(true)
                        hideProcessingLoader()
                    }
                })

            }
        }
    }

    private fun showErrorMessage(error: AppException) {
        error.apply {
            if (errCode == ZytrackConstant.BLOCKED_ERROR_CODE)
                isAccountBlocked.postValue(true)
            else
                showError(this,activityContext)
        }
    }

    private fun isValid(): Boolean {
        return when {
            email.value.isNullOrEmpty() -> {
                showToast(ZytrackConstant.ENTER_EMAIL)
                false
            }
            password.value.isNullOrEmpty() -> {
                showToast(ZytrackConstant.ENTER_PASSWORD)
                false
            }
            email.value?.isValidEmail() == false -> {
                showToast(ZytrackConstant.ENTER_VALID_EMAIL)
                false
            }
            password?.value?.length!! < 6 -> {
                showToast(ZytrackConstant.ENTER_VALID_PASSWORD)
                false
            }
            else -> true
        }
    }

    //end region
}