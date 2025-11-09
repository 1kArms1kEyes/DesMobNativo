package com.example.appmobile.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.entities.CartItem

@Dao
interface CartItemDao {
    @Insert
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE cart_item_id = :id")
    suspend fun getCartItemById(id: Int): CartItem?
}