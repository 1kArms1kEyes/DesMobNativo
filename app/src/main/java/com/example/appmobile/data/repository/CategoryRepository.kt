package com.example.appmobile.data.repository

import kotlinx.coroutines.flow.Flow

import com.example.appmobile.data.dao.CategoryDao
import com.example.appmobile.data.entities.Category

class CategoryRepository(private val dao: CategoryDao) {
    val allCategories: Flow<List<Category>> = dao.getAllCategories()

    suspend fun insert(category: Category) {
        dao.insertCategory(category)
    }

    suspend fun update(category: Category) {
        dao.updateCategory(category)
    }

    suspend fun delete(category: Category) {
        dao.deleteCategory(category)
    }

    suspend fun getById(id: Int): Category? {
        return dao.getCategoryById(id)
    }
}
