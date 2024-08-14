package com.example.aprajitafoundation.model

import androidx.annotation.DrawableRes

data class NameItem(
    @DrawableRes
    val nameImageResourceId: Int,
    val Name: String,
    val designation: String
)