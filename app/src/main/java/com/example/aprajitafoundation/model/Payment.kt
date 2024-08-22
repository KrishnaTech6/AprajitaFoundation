package com.example.aprajitafoundation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class Payment(
    val name: String,
    val email: String,
    val amount: Double,
    val phone: String,
    val date: Date = Date(),
    val razorpay_order_id: String,
    val razorpay_payment_id: String?,
    val razorpay_signature: String
): Parcelable

