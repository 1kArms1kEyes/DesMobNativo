package com.example.appmobile.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.entities.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE category_id = :id")
    suspend fun getCategoryById(id: Int): Category?
}
