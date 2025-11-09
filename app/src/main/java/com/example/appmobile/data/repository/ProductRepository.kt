package com.example.appmobile.data.repository

import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.dao.ProductDao
import com.example.appmobile.data.entities.Product

class ProductRepository(private val dao: ProductDao) {
    val allProducts: Flow<List<Product>> = dao.getAllProducts()

    suspend fun insert(product: Product) {
        dao.insertProduct(product)
    }

    suspend fun update(product: Product) {
        dao.updateProduct(product)
    }

    suspend fun delete(product: Product) {
        dao.deleteProduct(product)
    }

    suspend fun getById(id: Int): Product? {
        return dao.getProductById(id)
    }
}