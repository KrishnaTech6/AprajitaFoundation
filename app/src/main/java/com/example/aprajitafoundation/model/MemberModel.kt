package com.example.aprajitafoundation.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


//For receiveing member data where Socials is an object
@Parcelize
data class MemberModel(
    @SerializedName("_id")
    val id: String,
    var name: String,
    var position: String,
    var image: String,
    var description: String,
    var quote: String? = null,
    var socials: Socials? = null
):Parcelable

//For sending socials data as a json string
@Parcelize
data class MemberModel2(
    @SerializedName("_id")
    val id: String,
    var name: String,
    var position: String,
    var image: String,
    var description: String,
    var quote: String? = null,
    var socials: String?
):Parcelable

@Parcelize
data class Socials(
    var facebook: String = "",
    var twitter: String = "",
    var instagram: String = "",
    var linkedin: String= ""
):Parcelable