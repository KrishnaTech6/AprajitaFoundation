package com.example.aprajitafoundation.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.model.MemberItem

class ImageAdapter(private val context: Context, private val memberItem: List<MemberItem>, private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(memberItem: MemberItem)
    }

    class ImageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.iv_person)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvDesignation: TextView = view.findViewById(R.id.tv_designation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.name_item, parent, false)

        return ImageViewHolder(adapterLayout)
    }

    override fun getItemCount()= memberItem.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = memberItem[position]

        // Using Glide for image loading
        Glide.with(context)
            .load(item.nameImageResourceId)
            .into(holder.ivImage)

        holder.tvName.text = item.Name ?: ""
        holder.tvDesignation.text = item.designation ?: ""

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(item)

        }
    }
}