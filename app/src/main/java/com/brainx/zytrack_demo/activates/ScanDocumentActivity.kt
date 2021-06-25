package com.brainx.zytrack_demo.activates

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import com.brainx.zytrack_demo.base.BaseActivity
import com.brainx.zytrack_demo.databinding.ActivityScanDocumentBinding
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.brainx.zytrack_demo.viewModels.ScanDocumentViewModel
import com.example.monscanner.ScanActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException

@AndroidEntryPoint
class ScanDocumentActivity : BaseActivity<ScanDocumentViewModel, ActivityScanDocumentBinding>()  {
    //    region private properties
    override val mViewModel: ScanDocumentViewModel by viewModels()
    //    endregion
    // lifecycle region
    override fun getViewBinding() = ActivityScanDocumentBinding.inflate(layoutInflater)

    override fun customOnCreate(savedInstanceState: Bundle?) {
        super.customOnCreate(savedInstanceState)
        init()
    }

    private fun init(){
        setBinding()
    }

    private fun setBinding(){
        mViewBinding.apply {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mViewModel.apply {
            with(ZytrackConstant){
                try {
                    val imageUri =  data?.extras?.getSerializable(ScanActivity.SCAN_RESULT) as File
                    imageUri?.let {
                        when(requestCode){
                            CAMERA_FRAGMENT_CROP_REQUEST_CODE-> cameraImageUri.postValue(it)
                            GALLERY_FRAGMENT_CROP_REQUEST_CODE-> galleryImageUri.postValue(it)
                        }
                    }
                }catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }
}