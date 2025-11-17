package com.example.appmobile.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.entities.Cart
import com.example.appmobile.data.entities.CartSummary

@Dao
interface CartDao {

    @Insert
    suspend fun insertCart(cart: Cart): Long

    @Update
    suspend fun updateCart(cart: Cart)

    @Delete
    suspend fun deleteCart(cart: Cart)

    @Query("SELECT * FROM carts")
    fun getAllCarts(): Flow<List<Cart>>

    @Query("SELECT * FROM carts WHERE cart_id = :id")
    suspend fun getCartById(id: Int): Cart?


    @Query(
        """
        SELECT 
            c.cart_id AS cartId,
            c.creation_date AS creationDate,
            c.total_price AS totalPrice,
            MIN(p.name) AS productName
        FROM carts c
        JOIN cart_items ci ON ci.cart_id = c.cart_id
        JOIN products p ON p.product_id = ci.product_id
        GROUP BY c.cart_id
        ORDER BY c.creation_date DESC
        """
    )
    fun getCartSummaries(): Flow<List<CartSummary>>
}
