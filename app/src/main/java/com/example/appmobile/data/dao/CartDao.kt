package com.example.appmobile.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.entities.Cart

@Dao
interface CartDao {
    @Insert
    suspend fun insertCart(cart: Cart)

    @Update
    suspend fun updateCart(cart: Cart)

    @Delete
    suspend fun deleteCart(cart: Cart)

    @Query("SELECT * FROM carts")
    fun getAllCarts(): Flow<List<Cart>>

    @Query("SELECT * FROM carts WHERE cart_id = :id")
    suspend fun getCartById(id: Int): Cart?
}