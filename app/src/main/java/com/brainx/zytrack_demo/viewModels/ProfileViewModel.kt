package com.brainx.zytrack_demo.viewModels

import android.content.Context
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.brainx.zytrack_demo.api.SharedPreference
import com.brainx.zytrack_demo.base.BaseViewModel
import com.brainx.zytrack_demo.datastore.PreferenceDataStore
import com.brainx.zytrack_demo.models.UserModel
import com.brainx.zytrack_demo.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext

class ProfileViewModel @ViewModelInject constructor(
@ApplicationContext
val context: Context,
private val sharedPreference: SharedPreference,
private val authRepository: AuthRepository,
private val preferenceDataStore: PreferenceDataStore,
) : BaseViewModel() {
    // region public properties
    var user = ObservableField<UserModel>(UserModel())
    // end region
}