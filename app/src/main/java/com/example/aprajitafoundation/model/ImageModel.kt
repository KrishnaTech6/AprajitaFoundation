package com.example.aprajitafoundation.model

import com.google.gson.annotations.SerializedName

data class ImageModel(
    @SerializedName("_id")
    val id: String?,
    var image: String
)
