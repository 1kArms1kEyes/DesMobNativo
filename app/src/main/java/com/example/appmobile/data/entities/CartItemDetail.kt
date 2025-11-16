package com.example.appmobile.data.entities

data class CartItemDetail(
    val cartItemId: Int,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val lineTotal: Double,
    val imageUrl: String,
    val size: String
)
