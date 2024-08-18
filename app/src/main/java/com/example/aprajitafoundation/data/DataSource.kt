package com.example.aprajitafoundation.data

import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.model.MemberItem
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

    fun loadNameData(): List<MemberItem>{ //to load image , name , title of a person

        return listOf<MemberItem>(
            MemberItem(R.drawable.apj4, "Soni Nilu Jha", "Founder"),
            MemberItem(R.drawable.qwe8, "Shriya Jha", ""),
            MemberItem(R.drawable.apjname, "Dhiraj Jha", ""),
            MemberItem(R.drawable.apjname2, "", ""),


             )

    }

    fun loadImageData(): List<SlideItem>{

        return listOf<SlideItem>(
            SlideItem(R.drawable.qwe3, ""),
            SlideItem(R.drawable.qwe5, "Rangnayika"),
            SlideItem(R.drawable.qwe6, "Motivation"),
            SlideItem(R.drawable.qwe7, ""),
            SlideItem(R.drawable.qwe8, ""),
            SlideItem(R.drawable.qwe9, "Women Empowerment"),
            SlideItem(R.drawable.qwe10, ""),
            SlideItem(R.drawable.qwe11, ""),
            SlideItem(R.drawable.qwe12, ""),
            SlideItem(R.drawable.apj4, ""),
            SlideItem(R.drawable.apj5, ""),
            SlideItem(R.drawable.apj6, ""),
            SlideItem(R.drawable.apj7, ""),



            )

    }

    fun loadImageData2(): List<SlideItem>{

        return listOf<SlideItem>(
            SlideItem(R.drawable.qwe12, ""),
            SlideItem(R.drawable.apj4, ""),
            SlideItem(R.drawable.apj5, ""),
            SlideItem(R.drawable.qwe9, "Women Empowerment"),
            SlideItem(R.drawable.qwe10, ""),
            SlideItem(R.drawable.qwe11, ""),
            SlideItem(R.drawable.apj6, ""),
            SlideItem(R.drawable.apj7, ""),
            SlideItem(R.drawable.qwe3, ""),
            SlideItem(R.drawable.qwe5, "Rangnayika"),
            SlideItem(R.drawable.qwe6, "Motivation"),
            SlideItem(R.drawable.qwe7, ""),
            SlideItem(R.drawable.qwe8, ""),

            )

    }
}