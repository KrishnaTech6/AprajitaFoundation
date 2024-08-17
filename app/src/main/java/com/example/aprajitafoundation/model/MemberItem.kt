package com.example.aprajitafoundation.model

import org.bson.Document


data class MemberItem(

    val nameImageResourceId: Int,
    val name: String,
    val designation: String,

    ){
    // Convert NameItem to MongoDB Document
    fun toDocument(): Document {
        return Document("nameImageResourceId", nameImageResourceId)
            .append("name", name)
            .append("designation", designation)
    }

    // Convert MongoDB Document to NameItem
    companion object {
        fun fromDocument(document: Document): MemberItem {
            return MemberItem(
                nameImageResourceId = document.getInteger("image"),
                name = document.getString("name"),
                designation = document.getString("designation")
            )
        }
    }

}