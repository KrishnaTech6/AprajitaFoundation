package com.example.aprajitafoundation.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.util.Date

@Parcelize
data class EventModel(
    @SerializedName("_id")
    val id:String,
    val title: String,
    val description: String,
    val date: Date,
    val location: String,
    val image: String,
):Parcelable