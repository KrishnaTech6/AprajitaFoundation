package com.example.aprajitafoundation.data

import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.model.SlideItem

class DataSource {

    fun loadSliderData(): List<SlideItem>{

        return listOf<SlideItem>(
            SlideItem(R.drawable.apj1, "Aprajita Foundation"),
            SlideItem(R.drawable.apj3, "Aprajita Foundation"),
            SlideItem(R.drawable.apj4, "Aprajita Foundation"),
            SlideItem(R.drawable.apj5, "Aprajita Foundation"),
            SlideItem(R.drawable.apj6, "Aprajita Foundation"),
            SlideItem(R.drawable.apj7, "Aprajita Foundation"),

        )
    }

}