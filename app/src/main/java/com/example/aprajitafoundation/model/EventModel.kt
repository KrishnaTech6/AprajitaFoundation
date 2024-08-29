package com.example.aprajitafoundation.model

import com.google.gson.annotations.SerializedName
import java.text.DateFormat
import java.util.Date


data class EventModel(
    @SerializedName("_id")
    val id:String,
    val title: String,
    val description: String,
    val date: Date,
    val location: String,
    val image: String,
)