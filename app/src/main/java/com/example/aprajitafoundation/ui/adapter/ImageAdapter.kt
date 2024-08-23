package com.example.aprajitafoundation.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.model.MemberModel

class ImageAdapter(private val context: Context, private var memberItem: List<MemberModel>, private val onItemClickListener: (MemberModel) -> Unit) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.iv_person)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvDesignation: TextView = view.findViewById(R.id.tv_designation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.member_item, parent, false)

        return ImageViewHolder(adapterLayout)
    }

    override fun getItemCount()= memberItem.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = memberItem[position]

        // Using Glide for image loading
        Glide.with(context)
            .load(item?.image)
            .thumbnail(0.1f)
            .into(holder.ivImage)

        holder.tvName.text = item.name ?: ""
        holder.tvDesignation.text = item.position ?: ""

        holder.itemView.setOnClickListener {
            onItemClickListener(item)

        }
    }

    // Method to update the list of members
    fun updateMembers(newMembers: List<MemberModel>) {
        memberItem = newMembers
        notifyDataSetChanged()
    }
}