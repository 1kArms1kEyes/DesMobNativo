package com.example.appmobile.data.entities

import androidx.room.*

import com.example.appmobile.data.entities.User

@Entity(
    tableName = "carts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id")]
)
data class Cart(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cart_id")
    var cartId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "creation_date")
    val creationDate: String,

    @ColumnInfo(name = "total_price")
    var totalPrice: Double,

    @ColumnInfo(name = "payment_method")
    val paymentMethod: String,

    val status: String
)