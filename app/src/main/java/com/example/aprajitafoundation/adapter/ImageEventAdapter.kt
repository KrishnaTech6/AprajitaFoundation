package com.example.aprajitafoundation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.model.SlideItem

class ImageEventAdapter(private val context: Context, private val items: List<SlideItem>) :
    RecyclerView.Adapter<ImageEventAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_image_item)
        val textView: TextView = itemView.findViewById(R.id.tv_image_title_slider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_event_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = items[position]
        // Using Glide for image loading
        Glide.with(context)
            .load(item.imageResourceId)
            .into(holder.imageView)

        holder.textView.text = item.title ?: ""

        holder.itemView.setOnClickListener {
            // Implement the on-click action
            // Example: Toast showing the item title
            Toast.makeText(context, "Clicked on: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}