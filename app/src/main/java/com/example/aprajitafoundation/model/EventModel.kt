package com.example.aprajitafoundation.model

import java.text.DateFormat
import java.util.Date


data class EventModel(
    val id:String,
    val title: String,
    val description: String,
    val date: Date,
    val location: String,
    val image: String,
)