package com.example.appmobile.data.dao

import androidx.room.*
import com.example.appmobile.data.entities.Category
import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.entities.Product

@Dao
interface ProductDao {
    @Insert
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE product_id = :id")
    suspend fun getProductById(id: Int): Product?
}