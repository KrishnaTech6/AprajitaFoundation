package com.example.aprajitafoundation

import com.example.aprajitafoundation.data.Constants
import com.example.aprajitafoundation.model.MemberItem
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

class MongoDbClass {
    private lateinit var database: MongoDatabase
    fun initiateDb(){
        //Connecting with Mongodb
        val connectionString = Constants.conn_string_mongodb
        val mongoClient: MongoClient = MongoClients.create(connectionString)
        database = mongoClient.getDatabase("Aprajita")
    }

    fun insertNameItem(memberItem: MemberItem) {
        val collection: MongoCollection<Document> = database.getCollection("nameItems")
        collection.insertOne(memberItem.toDocument())
    }

    fun getNameItems(): List<MemberItem> {
        val collection: MongoCollection<Document> = database.getCollection("memberData")
        val nameItems = mutableListOf<MemberItem>()

        val cursor = collection.find()
        cursor.forEach { document ->
            nameItems.add(MemberItem.fromDocument(document))
        }

        return nameItems
    }
}

