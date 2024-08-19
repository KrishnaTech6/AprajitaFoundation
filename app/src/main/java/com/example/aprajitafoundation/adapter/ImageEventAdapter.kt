package com.example.aprajitafoundation.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.activities.FullScreenImageActivity
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.model.ImageModel

class ImageEventAdapter(
    private val context: Context,
    private val imageItems: List<ImageModel> = emptyList(),
    private val eventItems: List<EventModel> = emptyList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_EVENT = 1
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_image_item)
        val textView: TextView = itemView.findViewById(R.id.tv_image_title_slider)
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImage: ImageView = itemView.findViewById(R.id.event_image)
        val eventTitle: TextView = itemView.findViewById(R.id.event_title)
        val eventDescription: TextView = itemView.findViewById(R.id.event_description)
        val eventLocation: TextView = itemView.findViewById(R.id.event_location)
        val eventDate: TextView = itemView.findViewById(R.id.event_date)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < imageItems.size) {
            TYPE_IMAGE
        } else {
            TYPE_EVENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_IMAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.gallery_image_item, parent, false)
                ImageViewHolder(view)
            }
            TYPE_EVENT -> {
                val view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false)
                EventViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_IMAGE -> {
                val item = imageItems.getOrNull(position)
                val imageHolder = holder as ImageViewHolder
                Glide.with(context)
                    .load(item?.image)
                    .into(imageHolder.imageView)
                imageHolder.itemView.setOnClickListener {
                    val intent = Intent(context, FullScreenImageActivity::class.java)
                    intent.putExtra("image_url", item?.image)
                    context.startActivity(intent)
                }
            }
            TYPE_EVENT -> {
                val item = eventItems.getOrNull(position - imageItems.size)
                val eventHolder = holder as EventViewHolder
                Glide.with(context)
                    .load(item?.image)
                    .into(eventHolder.eventImage)
                eventHolder.eventTitle.text = item?.title
                eventHolder.eventDescription.text = item?.description
                eventHolder.eventLocation.text = item?.location
                eventHolder.eventDate.text = item?.date.toString() // Make sure date is in string format
            }
        }
    }

    override fun getItemCount(): Int {
        return imageItems.size + eventItems.size
    }
}
