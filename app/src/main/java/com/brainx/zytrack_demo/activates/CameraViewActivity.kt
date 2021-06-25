package com.brainx.zytrack_demo.activates

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.brainx.androidext.ext.openSettings
import com.brainx.androidext.ext.runTimePermissions
import com.brainx.zytrack_demo.base.BaseActivity
import com.brainx.zytrack_demo.databinding.ActivityCameraBinding
import com.brainx.zytrack_demo.utils.ZytrackConstant.FILENAME_FORMAT
import com.brainx.zytrack_demo.utils.checkCameraPermission
import com.brainx.zytrack_demo.utils.getBitmap
import com.brainx.zytrack_demo.utils.getCameraPermission
import com.brainx.zytrack_demo.utils.openDialog
import com.brainx.zytrack_demo.viewModels.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

@AndroidEntryPoint
class CameraViewActivity : BaseActivity<CameraViewModel, ActivityCameraBinding>() {

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var fileUri: Uri? = null

    //    region private properties
    override val mViewModel: CameraViewModel by viewModels()
    //    endregion
    // lifecycle region
    override fun getViewBinding() = ActivityCameraBinding.inflate(layoutInflater)

    override fun customOnCreate(savedInstanceState: Bundle?) {
        super.customOnCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        init()
    }

    private fun init(){
        setBinding()
        requestCameraPermission()
    }

    private fun setBinding(){
        mViewBinding.apply {
            viewModel = mViewModel
           listener = this@CameraViewActivity
        }
    }

    private fun requestCameraPermission(){
        mViewModel.apply {
            runTimePermissions(getCameraPermission()) { permissionGranted, isPermanentlyDenied ->
                if (!permissionGranted && !isPermanentlyDenied) {
                    showErrorDialog("Camera permission denied")
                }
                if (permissionGranted && !isPermanentlyDenied) {
                    openCamera()
                }
                if (isPermanentlyDenied) {
                    openDialog(
                        this@CameraViewActivity,
                        btn_Text = "Go to Settings",
                        title = "Camera Permission Request",
                        message = "Need Camera Permission to use the feature"
                    ){
                        if (it){
                            openSettings()
                        }
                    }
                }
            }
        }
    }

    private fun openCamera(){
        if (checkCameraPermission(this)){
            return
        }else{
            startCamera()
        }
    }

    fun capturePhoto(view: View){
        takePhoto()
    }

    fun deleteDirectory(view: View){
        clearTempImages()
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

//         Create time-stamped output file to hold the image
        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = photoFile?.let { ImageCapture.OutputFileOptions.Builder(it).build() }

        // Set up image capture listener, which is triggered after photo has
        // been taken
        if (outputOptions != null) {
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        mViewModel.showErrorDialog("Photo capture failed: ${exc.message}")
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)
                        mViewBinding.ivCapture.setImageBitmap(getBitmap(savedUri))
                        val msg = "Photo capture succeeded: $savedUri"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        mViewModel.showSuccessDialog(msg)
                    }
                })
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        showToastMessage("Average luminosity: $luma")
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

            } catch (exc: Exception) {
                mViewModel.showErrorDialog("Use case binding failed")
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "Doc Scanner").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun clearTempImages() {
        try {

           val deleted = getOutputDirectory().deleteRecursively()
            mViewModel.showSuccessDialog(deleted.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }



    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

    private fun rotate(){
        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation : Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation : Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
        orientationEventListener.enable()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}