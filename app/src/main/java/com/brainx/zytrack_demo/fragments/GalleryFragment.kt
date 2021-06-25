package com.brainx.zytrack_demo.fragments

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.brainx.zytrack_demo.activates.ScanDocumentActivity
import com.brainx.zytrack_demo.base.BaseFragment
import com.brainx.zytrack_demo.databinding.FragmentGalleryBinding
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.brainx.zytrack_demo.viewModels.ScanDocumentViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


@AndroidEntryPoint
class GalleryFragment : BaseFragment<ScanDocumentViewModel, FragmentGalleryBinding>() {
    //    region private properties
    private lateinit var requiredActivity: ScanDocumentActivity
    override val mViewModel: ScanDocumentViewModel by activityViewModels()
    //    endregion
    // lifecycle region
    override fun getViewBinding() = FragmentGalleryBinding.inflate(layoutInflater)

    override fun customOnViewCreated(savedInstanceState: Bundle?) {
        init()
    }
    //    end region
    //region private Methods
    private fun init() {
        requiredActivity = requireActivity() as ScanDocumentActivity
        setBinding()

    }

    private fun setBinding(){
        mViewBinding.apply {
            requiredActivity.apply {
                mViewModel.imageUri.observe(this, {
                    Log.d("SCAN_GALLERY_FRAGMENT", "observalble ${it.toString()}")
                    try {
                        mViewBinding.captureImage.setImageBitmap(getBitmap(contentResolver,it))
                    }catch (e:Exception){
                        e.message?.let { it1 -> mViewModel.showErrorDialog(it1) }
                    }
//                    val resource = ZytrackConstant.NO_IMAGE_URL
//                    Glide.with(mViewBinding.captureImage.context)
//                        .setDefaultRequestOptions(RequestOptions())
//                        .load(resource)
//                        .into(mViewBinding.captureImage)
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
}