package com.example.aprajitafoundation.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.aprajitafoundation.activities.FullScreenImageActivity
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.model.ImageModel
import com.example.aprajitafoundation.model.SlideItem

class ImageEventAdapter(private val context: Context, private val items: List<ImageModel>?) :
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
        val item = items?.get(position)
        // Using Glide for image loading
        Glide.with(context)
            .load(item?.image)
            .into(holder.imageView)

        Log.d("Image", "${item?.image}")

//        holder.textView.text = item. ?: ""

//        if (item.title.isNullOrBlank()){
//            holder.textView.visibility = View.GONE
//        }else{
//            holder.textView.visibility = View.VISIBLE
//        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, FullScreenImageActivity::class.java)
            intent.putExtra("image_url", item?.image)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }
}