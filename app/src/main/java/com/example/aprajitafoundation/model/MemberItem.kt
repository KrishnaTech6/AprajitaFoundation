package com.example.aprajitafoundation.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MemberItem(
    @DrawableRes
    val nameImageResourceId: Int,
    val Name: String,
    val designation: String
):Parcelable