package com.brainx.zytrack_demo.viewholders

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brainx.zytrack_demo.R
import com.brainx.zytrack_demo.databinding.GalleryItemLayoutBinding
import com.brainx.zytrack_demo.utils.ZytrackConstant
import com.brainx.zytrack_demo.utils.getBitmap

class GalleryViewHolder(val itemBinding:GalleryItemLayoutBinding) :RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(position: Int,context : Context, map: Map<String,Any?>) {
        itemBinding.apply {
            ivPhoto.setImageBitmap(getBitmap(context.contentResolver,map[ZytrackConstant.CROPPED_PHOTO_KEY] as Uri))
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
