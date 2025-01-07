package com.example.aprajitafoundation.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class EventModel(
    @SerializedName("_id")
    val id:String,
    var title: String,
    var description: String,
    var date: Date,
    var location: String,
    var image: String,
):Parcelable