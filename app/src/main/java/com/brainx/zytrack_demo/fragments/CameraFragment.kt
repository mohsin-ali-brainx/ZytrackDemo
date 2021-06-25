package com.brainx.zytrack_demo.fragments

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.brainx.androidext.ext.openSettings
import com.brainx.androidext.ext.runTimePermissions
import com.brainx.zytrack_demo.activates.ScanDocumentActivity
import com.brainx.zytrack_demo.base.BaseFragment
import com.brainx.zytrack_demo.databinding.FragmentCameraBinding
import com.brainx.zytrack_demo.utils.*
import com.brainx.zytrack_demo.viewModels.ScanDocumentViewModel
import com.example.monscanner.ScanActivity
import com.example.monscanner.ScanConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit
@AndroidEntryPoint
class CameraFragment : BaseFragment<ScanDocumentViewModel, FragmentCameraBinding>() {



    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    //    region private properties
    private lateinit var requiredActivity: ScanDocumentActivity
    override val mViewModel: ScanDocumentViewModel by activityViewModels()
    //    endregion
    // lifecycle region
    override fun getViewBinding() = FragmentCameraBinding.inflate(layoutInflater)

    override fun customOnViewCreated(savedInstanceState: Bundle?) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        init()
    }
    //    end region
    //region private Methods
    private fun init() {
        requiredActivity = requireActivity() as ScanDocumentActivity
        setBinding()
        requestCameraPermission()
        uriObservable()
    }

    private fun setBinding(){
        mViewBinding.apply {
            viewModel = mViewModel
            listener = this@CameraFragment
        }
    }


    private fun requestCameraPermission(){
        mViewModel.apply {
            requiredActivity.apply {
                runTimePermissions(getCameraPermission()) { permissionGranted, isPermanentlyDenied ->
                    if (!permissionGranted && !isPermanentlyDenied) {
                        showErrorDialog("Camera permission denied")
                    }
                    if (permissionGranted && !isPermanentlyDenied) {
                        openCamera()
                    }
                    if (isPermanentlyDenied) {
                        openDialog(
                            this,
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
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun openCamera(){
        if (checkCameraPermission(requiredActivity)){
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
        mViewBinding.ivCapture.setImageBitmap(null)
    }

    fun moveToGallery(view: View){
        val showGallery = true
        val action = CameraFragmentDirections.navigateToGallery(showGallery)
        Navigation.findNavController(mViewBinding.root).navigate(action)
    }

    private fun uriObservable(){
        requiredActivity.apply {
            with(mViewModel) {
               cameraImageUri.observe(this@apply, {
                   try {
                       imageFileList.add(
                           mapOf<String, Any?>(
                               ZytrackConstant.ORIGINAL_FILE_KEY to fileObservable.get() as File,
                               ZytrackConstant.CROPPED_PHOTO_KEY to it as File
                           )
                       )
                       var original = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(it))
                       mViewBinding.ivCapture.setImageBitmap(original)
                   } catch (e: java.lang.Exception) {

                   }
               })
            }
        }
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun getBitmap(cr: ContentResolver, url: Uri?): Bitmap {
        val input: InputStream? = url?.let { cr.openInputStream(it) }
        val bitmap = BitmapFactory.decodeStream(input)
        input?.close()
        return bitmap
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        val directory = getOutputDirectory()
        ScanConstants.IMAGE_PATH = directory.path
//         Create time-stamped output file to hold the image
        val photoFile = File(
            directory,
            SimpleDateFormat(
                ZytrackConstant.FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = photoFile?.let { ImageCapture.OutputFileOptions.Builder(it).build() }

        // Set up image capture listener, which is triggered after photo has
        // been taken
        if (outputOptions != null) {
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requiredActivity),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {

                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)
                        mViewModel.fileObservable.set(photoFile)
                        ScanConstants.PHOTO_FILE = photoFile

                        requiredActivity.apply {
                            val intent = Intent(this, ScanActivity::class.java)
                            intent.putExtra(
                                ScanConstants.OPEN_INTENT_PREFERENCE,
                                ScanConstants.OPEN_CAMERA
                            )
                            startActivityForResult(
                                intent,
                                ZytrackConstant.CAMERA_FRAGMENT_CROP_REQUEST_CODE
                            )
                        }
                    }
                })
        }
    }

    private fun startCamera() {
        requiredActivity.apply {
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

                }

            }, ContextCompat.getMainExecutor(this))
        }
    }


    private fun getOutputDirectory(): File {
        requiredActivity.apply {
            val mediaDir = externalMediaDirs.firstOrNull()?.let {
                File(it, "Doc Scanner").apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else filesDir
        }
    }

    private fun clearTempImages() {
        try {
            val deleted = getOutputDirectory().deleteRecursively()
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
            override fun onOrientationChanged(orientation: Int) {
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

}