package com.example.aprajitafoundation.model

import androidx.annotation.DrawableRes

data class SlideItem(
    @DrawableRes
    val imageResourceId: Int,
    val title: String
)