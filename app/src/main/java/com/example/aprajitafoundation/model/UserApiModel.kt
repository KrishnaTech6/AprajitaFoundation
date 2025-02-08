package com.example.aprajitafoundation.model


data class ImagesRequest(val images: List<String>)
data class GenericResponse(val message: String)
data class PaymentRequest(val amount: Double)
data class PaymentResponse(val order: Order)

data class PaymentDetailResponse(val payments: List<Payment>)
data class Order(
    val id: String,
    val amount: Int,
    val currency: String,
    val receipt: String?,
    val status: String?
)