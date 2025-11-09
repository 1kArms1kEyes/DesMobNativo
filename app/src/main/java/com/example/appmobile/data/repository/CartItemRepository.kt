package com.example.appmobile.data.repository

import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.dao.CartItemDao
import com.example.appmobile.data.entities.CartItem

class CartItemRepository(private val dao: CartItemDao) {
    val allCartItems: Flow<List<CartItem>> = dao.getAllCartItems()

    suspend fun insert(cartItem: CartItem) {
        dao.insertCartItem(cartItem)
    }

    suspend fun update(cartItem: CartItem) {
        dao.updateCartItem(cartItem)
    }

    suspend fun delete(cartItem: CartItem) {
        dao.deleteCartItem(cartItem)
    }

    suspend fun getById(id: Int): CartItem? {
        return dao.getCartItemById(id)
    }
}