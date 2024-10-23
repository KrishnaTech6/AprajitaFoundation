package com.example.aprajitafoundation.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aprajitafoundation.ui.activities.FullScreenImageActivity
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.utility.AnimationUtils
import com.example.aprajitafoundation.model.EventModel
import com.example.aprajitafoundation.model.ImageModel
import com.example.aprajitafoundation.viewmodel.DataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageEventAdapter(
    private val context: Context,
    private val imageItems: List<ImageModel> = emptyList(),
    private val eventItems: List<EventModel> = emptyList(),
    private val isSlider: Boolean = false,
    private val isAdmin: Boolean = false,
    private val viewModel: DataViewModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_EVENT = 1
        private const val TYPE_IMAGE_SLIDER = 3
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_image_item)
        val deleteImage: ImageView = itemView.findViewById(R.id.iv_del_gallery)
    }

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_slider_item)
        val textView: TextView = itemView.findViewById(R.id.tv_title_slider)
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView as CardView
        val eventImage: ImageView = itemView.findViewById(R.id.event_image)
        val eventTitle: TextView = itemView.findViewById(R.id.event_title)
        val eventDescription: TextView = itemView.findViewById(R.id.event_description)
        val eventLocation: TextView = itemView.findViewById(R.id.event_location)
        val eventDate: TextView = itemView.findViewById(R.id.event_date)

        val llDelEditEvents: LinearLayout = itemView.findViewById(R.id.ll_admin_del_edit_event)
        val editEvent: ImageView = itemView.findViewById(R.id.edit_event)
        val deleteEvent: ImageView = itemView.findViewById(R.id.del_event)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < imageItems.size) {
            if (isSlider) {
                TYPE_IMAGE_SLIDER
            } else TYPE_IMAGE
        } else {
            TYPE_EVENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_IMAGE -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.gallery_image_item, parent, false)
                ImageViewHolder(view)
            }

            TYPE_IMAGE_SLIDER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.slider_item, parent, false)
                SliderViewHolder(view)
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
                AnimationUtils.slideInFromBottom(imageHolder.imageView, 500)

                if (isAdmin) {
                    imageHolder.deleteImage.visibility = View.VISIBLE
                    imageHolder.deleteImage.setOnClickListener {

                        // Create an AlertDialog to ask
                        AlertDialog.Builder(context)
                            .setTitle("Delete Image")
                            .setMessage("Do you really want to delete the Image?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                dialog.dismiss()
                                //delete from the server
                                viewModel.deleteGalleryImage(context, item?.id)
                                Log.d("ViewModel" ,"itemId : ${item?.id}")
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                } else {
                    imageHolder.deleteImage.visibility = View.GONE
                }
                Glide.with(context)
                    .load(item?.image)
                    .into(imageHolder.imageView)
                imageHolder.itemView.setOnClickListener {
                    val intent = Intent(context, FullScreenImageActivity::class.java)
                    intent.putExtra(context.getString(R.string.image_url_bundle), item?.image)
                    context.startActivity(intent)
                }
            }

            TYPE_IMAGE_SLIDER -> {
                val item = imageItems.getOrNull(position)
                val imageHolder = holder as SliderViewHolder
                Glide.with(context)
                    .load(item?.image)
                    .into(imageHolder.imageView)
                imageHolder.itemView.setOnClickListener {
                    val intent = Intent(context, FullScreenImageActivity::class.java)
                    intent.putExtra(context.getString(R.string.image_url_bundle), item?.image)
                    context.startActivity(intent)
                }
            }

            TYPE_EVENT -> {
                val item = eventItems.getOrNull(position - imageItems.size)
                val formattedDate = formatDate(item?.date)
                val eventHolder = holder as EventViewHolder
                AnimationUtils.slideInFromBottom(eventHolder.eventImage, 500)

                if (isAdmin) {
                    eventHolder.llDelEditEvents.visibility = View.VISIBLE
                    eventHolder.deleteEvent.setOnClickListener {

                        // Create an AlertDialog to ask
                        AlertDialog.Builder(context)
                            .setTitle("Delete Event")
                            .setMessage("Do you really want to delete the event?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                dialog.dismiss()
                                //delete from the server
                                viewModel.deleteEvent(context, item?.id)
                                Log.d("ViewModel" ,"itemId : ${item?.id}")
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    eventHolder.editEvent.setOnClickListener {
                        val bundle = Bundle().apply {
                            putParcelable(context.getString(R.string.event_parcelable), item)
                        }
                        val navController = (context as? AppCompatActivity)?.findNavController(R.id.nav_host_fragment_content_admin)
                        navController?.navigate(R.id.action_nav_events_admin_to_editEventFragment, bundle)
                    }
                } else {
                    eventHolder.llDelEditEvents.visibility = View.GONE
                }
                Glide.with(context)
                    .load(item?.image)
                    .thumbnail(0.1f)
                    .into(eventHolder.eventImage)
                eventHolder.eventTitle.text = item?.title
                eventHolder.eventDescription.text = item?.description
                eventHolder.eventLocation.text = item?.location
                eventHolder.eventDate.text = formattedDate

                eventHolder.eventImage.setOnClickListener{
                    val intent = Intent(context, FullScreenImageActivity::class.java)
                    intent.putExtra(context.getString(R.string.image_url_bundle), item?.image)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imageItems.size + eventItems.size
    }

    private fun formatDate(date: Date?): String {
        return date?.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.format(it)
        } ?: ""
    }
}
