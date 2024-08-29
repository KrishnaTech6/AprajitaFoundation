package com.example.aprajitafoundation.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class MemberModel(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val position: String,
    val image: String,
    val description: String,
    val quote: String? = null,
    val socials: Socials? = null
):Parcelable

@Parcelize
data class Socials(
    val facebook: String? = null,
    val twitter: String? = null,
    val instagram: String? = null,
    val linkedin: String? = null
):Parcelable