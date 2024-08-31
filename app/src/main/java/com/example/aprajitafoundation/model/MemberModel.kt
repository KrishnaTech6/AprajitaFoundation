package com.example.aprajitafoundation.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class MemberModel(
    @SerializedName("_id")
    val id: String,
    var name: String,
    var position: String,
    var image: String,
    var description: String,
    var quote: String? = null,
    val socials: Socials? = null
):Parcelable

@Parcelize
data class Socials(
    var facebook: String? = null,
    var twitter: String? = null,
    var instagram: String? = null,
    var linkedin: String? = null
):Parcelable