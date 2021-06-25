package com.brainx.zytrack_demo.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import com.brainx.zytrack_demo.base.BaseViewModel
import com.brainx.zytrack_demo.datastore.DataStore
import com.brainx.zytrack_demo.repository.AuthRepository
import com.brainx.zytrack_demo.sharedPreference.SharedPreference

class CameraViewModel  @ViewModelInject constructor(
    private val sharedPreference: SharedPreference,
    private val authRepository: AuthRepository,
    private val dataStore: DataStore,
) : BaseViewModel() {
}