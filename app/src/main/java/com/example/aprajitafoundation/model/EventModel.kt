package com.example.aprajitafoundation.model

import java.text.DateFormat

data class EventModel(
    val id:String,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val image: String,
)