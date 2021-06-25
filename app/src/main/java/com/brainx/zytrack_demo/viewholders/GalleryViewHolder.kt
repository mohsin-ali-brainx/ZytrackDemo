package com.brainx.zytrack_demo.viewholders

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brainx.zytrack_demo.R
import com.brainx.zytrack_demo.databinding.GalleryItemLayoutBinding
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.brainx.zytrack_demo.utils.getBitmap
import java.io.File

class GalleryViewHolder(val itemBinding:GalleryItemLayoutBinding) :RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(position: Int,context : Context, map: Map<String,Any?>) {
        itemBinding.apply {
            ivPhoto.setImageBitmap(MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(map[ZytrackConstant.CROPPED_PHOTO_KEY] as File)))
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): GalleryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: GalleryItemLayoutBinding = DataBindingUtil
                .inflate(
                    layoutInflater, R.layout.gallery_item_layout,
                    parent, false
                )
            return GalleryViewHolder(binding)
        }
    }
}
