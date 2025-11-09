package com.example.appmobile.data.entities

import androidx.room.*
import com.example.appmobile.data.entities.Product
import com.example.appmobile.data.entities.Cart

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Cart::class,
            parentColumns = ["cart_id"],
            childColumns = ["cart_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("product_id"), Index("cart_id")]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cart_item_id")
    var cartItemId: Int = 0,

    @ColumnInfo(name = "product_id")
    val productId: Int,

    @ColumnInfo(name = "cart_id")
    val cartId: Int,

    @ColumnInfo(name = "cart_item_quantity")
    val cartItemQuantity: Int
)
