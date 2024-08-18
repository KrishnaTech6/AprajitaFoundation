package com.example.aprajitafoundation.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SlideItem(
    @DrawableRes
    val imageResourceId: Int,
    val title: String
):Parcelable