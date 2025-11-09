package com.example.appmobile.tests

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.Product
import com.example.appmobile.ui.viewmodels.ProductViewModel
import com.example.appmobile.data.repository.ProductRepository
import com.example.appmobile.ui.viewmodels.ProductViewModelFactory

class TestProductActivity : AppCompatActivity() {
    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(
            ProductRepository(
                AppDatabase.getDatabase(applicationContext).productDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TestProductActivity", "Iniciando pruebas de Product...")

        // Crear producto
        lifecycleScope.launch {
            viewModel.insert(
                Product(
                    categoryId = 1,
                    name = "Chaqueta negra",
                    price = 120000.0,
                    description = "Chaqueta en cuero sintético",
                    size = "M",
                    stock = 15,
                    color = "Negro",
                    imageUrl = "https://ejemplo.com/chaqueta.jpg",
                    isActive = true
                )
            )
            viewModel.insert(
                Product(
                    categoryId = 2,
                    name = "Chaqueta negra",
                    price = 120000.0,
                    description = "Chaqueta en cuero sintético",
                    size = "M",
                    stock = 15,
                    color = "Negro",
                    imageUrl = "https://ejemplo.com/chaqueta.jpg",
                    isActive = true
                )
            )
            viewModel.insert(
                Product(
                    categoryId = 4,
                    name = "Chaqueta negra",
                    price = 120000.0,
                    description = "Chaqueta en cuero sintético",
                    size = "M",
                    stock = 15,
                    color = "Negro",
                    imageUrl = "https://ejemplo.com/chaqueta.jpg",
                    isActive = true
                )
            )

            viewModel.allProducts.collect { products ->
                Log.d("TestProductActivity", "Product actuales: $products")
            }

            // Buscar por ID
            viewModel.getById(1) { product ->
                Log.d("TestProductActivity", "Product con ID 1: $product")
            }

            // Actualizar
            viewModel.update(
                Product(
                    productId = 2,
                    categoryId = 1,
                    name = "Chaqueta negra",
                    price = 120000.0,
                    description = "Chaqueta en cuero sintético",
                    size = "S",
                    stock = 12,
                    color = "Negro",
                    imageUrl = "https://ejemplo.com/chaqueta.jpg",
                    isActive = true
                )
            )

            // Eliminar
            viewModel.delete(
                Product(
                    productId = 3,
                    categoryId = 4,
                    name = "Chaqueta negra",
                    price = 120000.0,
                    description = "Chaqueta en cuero sintético",
                    size = "M",
                    stock = 15,
                    color = "Negro",
                    imageUrl = "https://ejemplo.com/chaqueta.jpg",
                    isActive = true
                )
            )
        }
    }
}