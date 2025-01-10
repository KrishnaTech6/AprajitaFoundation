package com.example.aprajitafoundation

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp: Application(){
    override fun onCreate() {
        super.onCreate()
        //Initialize firebase
        FirebaseApp.initializeApp(this)
    }
}