package com.example.appmobile.data.repository

import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.dao.CartDao
import com.example.appmobile.data.entities.Cart

class CartRepository(private val dao: CartDao) {
    val allCarts: Flow<List<Cart>> = dao.getAllCarts()

    suspend fun insert(cart: Cart) {
        dao.insertCart(cart)
    }

    suspend fun update(cart: Cart) {
        dao.updateCart(cart)
    }

    suspend fun delete(cart: Cart) {
        dao.deleteCart(cart)
    }

    suspend fun getById(id: Int): Cart? {
        return dao.getCartById(id)
    }
}