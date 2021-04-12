package com.brainx.zytrack_demo.viewModels

import android.app.Activity
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brainx.zytrack_demo.User
import com.brainx.zytrack_demo.sharedPreference.SharedPreference
import com.brainx.zytrack_demo.base.BaseViewModel
import com.brainx.zytrack_demo.datastore.DataStore
import com.brainx.zytrack_demo.models.UserModel
import com.brainx.zytrack_demo.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel @ViewModelInject constructor(
    private val sharedPreference: SharedPreference,
    private val authRepository: AuthRepository,
    private val dataStore: DataStore,
) : BaseViewModel() {
    // region public properties
    var user = ObservableField<User>(User.getDefaultInstance())
    var logOutObserver = MutableLiveData<Boolean>()
    lateinit var requireActivity: Activity
    // end region
    fun logout(view: View) {
        showProcessingLoader()
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signOut({ apiResponse, status ->
                if (status) {
                    clearPrefs()
                }
            }, {
                hideProcessingLoader()
                showError(it, requireActivity)
            })
        }
    }

    private fun clearPrefs(){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.clearPreferenceDataStore({
                if (it) {
                    logOutObserver.postValue(it)
                    hideProcessingLoader()
                }
            })

        }
    }
}