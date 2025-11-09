package com.example.appmobile.tests

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.Category
import com.example.appmobile.data.repository.CategoryRepository
import com.example.appmobile.ui.viewmodels.CategoryViewModel
import com.example.appmobile.ui.viewmodels.CategoryViewModelFactory

class TestCategoryActivity : AppCompatActivity() {

    private val viewModel: CategoryViewModel by viewModels {
        CategoryViewModelFactory(
            CategoryRepository(
                AppDatabase.getDatabase(applicationContext).categoryDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TestCategoryActivity", "Iniciando pruebas de Category...")

        lifecycleScope.launch {
            viewModel.insert(Category(categoryName = "Hoodies"))
            viewModel.insert(Category(categoryName = "Camisetas"))
            viewModel.insert(Category(categoryName = "Snapbacks"))
            viewModel.insert(Category(categoryName = "Tenis"))

            // Observar todas las categorías
            viewModel.allCategories.collect { categories ->
                Log.d("TestCategoryActivity", "Categorías actuales: $categories")
            }

            // Buscar por ID
            viewModel.getById(1) { category ->
                Log.d("TestCategoryActivity", "Categoría con ID 1: $category")
            }

            // Actualizar
            viewModel.update(Category(categoryId = 1, categoryName = "Zapatillas"))

            // Eliminar
            viewModel.delete(Category(categoryId = 4, categoryName = "Tenis"))
        }
    }
}