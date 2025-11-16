package com.example.appmobile.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.appmobile.data.entities.CartItem
import com.example.appmobile.data.entities.CartItemDetail

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

    @Query(
        """
        SELECT 
            ci.cart_item_id AS cartItemId,
            p.name AS productName,
            p.price AS unitPrice,
            ci.cart_item_quantity AS quantity,
            (p.price * ci.cart_item_quantity) AS lineTotal,
            p.image_url AS imageUrl,
            p.size AS size
        FROM cart_items ci
        JOIN products p ON p.product_id = ci.product_id
        """
    )
    fun getCartItemsWithProduct(): Flow<List<CartItemDetail>>
}
