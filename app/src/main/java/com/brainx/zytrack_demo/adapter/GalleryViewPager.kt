package com.brainx.zytrack_demo.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brainx.zytrack_demo.viewholders.GalleryViewHolder

class GalleryViewPager (private val context: Context) :
    RecyclerView.Adapter<GalleryViewHolder>() {

    private var photoList: ArrayList<Map<String,Any?>> = arrayListOf()

    fun setData(data: List<Map<String,Any?>>) {
        photoList.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    fun getPhotoList():List<Map<String,Any?>>{
        return photoList
    }

    fun updateList(position:Int,map:Map<String,Any?>){
        photoList[position] = map
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder =
        GalleryViewHolder.from(parent)

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) =
        holder.bind(position,context, photoList[position])

    override fun getItemCount(): Int {
        return photoList.size
    }

}