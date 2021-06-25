package com.brainx.zytrack_demo.activates

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.Navigation
import com.brainx.zytrack_demo.R
import com.brainx.zytrack_demo.base.BaseActivity
import com.brainx.zytrack_demo.databinding.ActivityCameraBinding
import com.brainx.zytrack_demo.databinding.ActivityScanDocumentBinding
import com.brainx.zytrack_demo.viewModels.CameraViewModel
import com.brainx.zytrack_demo.viewModels.ScanDocumentViewModel
import com.example.monscanner.ScanActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*
import java.util.concurrent.Executors

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
        Log.d("SCAN_CAMERA_ACTIVITY", "requestcode:$requestCode and resultcode:$resultCode")
        try {
            assert(data != null)
            val imageUri = Objects.requireNonNull(data!!.extras)!!
                .getParcelable<Uri>(ScanActivity.SCAN_RESULT)!!
            Log.d("SCAN_CAMERA_FRAGMENT", "${imageUri.toString()}")
            mViewModel.imageUri.postValue(imageUri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}