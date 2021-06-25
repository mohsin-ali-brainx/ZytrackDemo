package com.brainx.zytrack_demo.viewModels

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.brainx.zytrack_demo.base.BaseViewModel
import com.brainx.zytrack_demo.datastore.DataStore
import com.brainx.zytrack_demo.repository.AuthRepository
import com.brainx.zytrack_demo.sharedPreference.SharedPreference
import java.io.File

class ScanDocumentViewModel @ViewModelInject constructor(
    private val sharedPreference: SharedPreference,
    private val authRepository: AuthRepository,
    private val dataStore: DataStore,
) : BaseViewModel() {
    var fileObservable = ObservableField<File>()
    var imageFileList = arrayListOf<Map<String,Any?>>()
    var cameraImageUri = MutableLiveData<File>()
    var galleryImageUri = MutableLiveData<File>()
}