package com.brainx.zytrack_demo.fragments

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.brainx.zytrack_demo.activates.ScanDocumentActivity
import com.brainx.zytrack_demo.adapter.GalleryViewPager
import com.brainx.zytrack_demo.base.BaseFragment
import com.brainx.zytrack_demo.databinding.FragmentGalleryBinding
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.brainx.zytrack_demo.utils.getBitmap
import com.brainx.zytrack_demo.viewModels.ScanDocumentViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.monscanner.ScanActivity
import com.example.monscanner.ScanConstants
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception


@AndroidEntryPoint
class GalleryFragment : BaseFragment<ScanDocumentViewModel, FragmentGalleryBinding>() {
    //    region private properties
    private lateinit var requiredActivity: ScanDocumentActivity
    override val mViewModel: ScanDocumentViewModel by activityViewModels()
    private var currentPosition = -1
    private lateinit var viewPagerAdapter : GalleryViewPager
    private val args: GalleryFragmentArgs  by navArgs()
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
        viewPagerAdapter = GalleryViewPager(requiredActivity)
        settingViewPager()
        setBinding()
        checkArgs()
        uriObservable()
    }

    private fun checkArgs(){
        if (args.showGallery){
            viewPagerAdapter.setData(mViewModel.imageFileList)
        }
    }

    private fun setBinding(){
        mViewBinding.apply {
            listener = this@GalleryFragment
        }
    }

    private fun settingViewPager() {
        mViewBinding.vpGallery.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        viewPagerOnScrollListener()
    }

    private fun viewPagerOnScrollListener() {
        mViewBinding.vpGallery.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                currentPosition = position
            }
        })
    }

    fun cropImage(view: View){
        requiredActivity.apply {
           ScanConstants.PHOTO_FILE =   viewPagerAdapter.getPhotoList()[currentPosition][ZytrackConstant.ORIGINAL_FILE_KEY] as File
            val intent = Intent(this, ScanActivity::class.java)
            intent.putExtra(
                ScanConstants.OPEN_INTENT_PREFERENCE,
                ScanConstants.OPEN_CAMERA
            )
            startActivityForResult(intent, ZytrackConstant.GALLERY_FRAGMENT_CROP_REQUEST_CODE)
        }
    }

    private fun uriObservable(){
        requiredActivity.apply {
            with(mViewModel) {
                galleryImageUri.observe(this@apply, {
                    try {
                        val previousMap = viewPagerAdapter.getPhotoList()[currentPosition]
                        val map = mapOf<String,Any?>(ZytrackConstant.ORIGINAL_FILE_KEY to previousMap[ZytrackConstant.ORIGINAL_FILE_KEY] as File,
                            ZytrackConstant.CROPPED_PHOTO_KEY to it as File
                        )
                        viewPagerAdapter.updateList(currentPosition,map)
                        imageFileList.apply {
                            clear()
                            addAll(viewPagerAdapter.getPhotoList())
                        }
                    }catch (e: java.lang.Exception){
                       // e.message?.let { it1 ->showErrorDialog(it1) }
                    }
                })
            }
        }
    }
}