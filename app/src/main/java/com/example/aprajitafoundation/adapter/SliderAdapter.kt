package com.example.aprajitafoundation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.activities.FullScreenImageActivity
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.model.SlideItem

class SliderAdapter(private val context: Context, private val items: List<SlideItem>) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_slider_item)
        val textView: TextView = itemView.findViewById(R.id.tv_title_slider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.slider_item, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val item = items[position]
        // Using Glide for image loading
        Glide.with(context)
            .load(item.imageResourceId)
            .into(holder.imageView)

        holder.textView.text = item.title ?: ""

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, FullScreenImageActivity::class.java)
            intent.putExtra("IMAGE_DATA", item)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}