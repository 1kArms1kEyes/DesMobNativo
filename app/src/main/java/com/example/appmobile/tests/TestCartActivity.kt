package com.example.appmobile.tests

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.Cart
import com.example.appmobile.data.repository.CartItemRepository
import com.example.appmobile.data.repository.CartRepository
import com.example.appmobile.ui.viewmodels.CartItemViewModel
import com.example.appmobile.ui.viewmodels.CartItemViewModelFactory
import com.example.appmobile.ui.viewmodels.CartViewModel
import com.example.appmobile.ui.viewmodels.CartViewModelFactory

class TestCartActivity : AppCompatActivity() {
    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(
            CartRepository(
                AppDatabase.getDatabase(applicationContext).cartDao()
            )
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TestCartActivity", "Iniciando pruebas de Cart...")

        lifecycleScope.launch {
            viewModel.insert(
                Cart(
                    userId = 1,
                    creationDate = "2025-10-25",
                    totalPrice = 70000.0,
                    paymentMethod = "Daviplata",
                    status = "Pendiente de pago"
                )
            )
            viewModel.insert(
                Cart(
                    userId = 1,
                    creationDate = "2025-10-28",
                    totalPrice = 12000.0,
                    paymentMethod = "Daviplata",
                    status = "Pendiente de pago"
                )
            )
            viewModel.insert(
                Cart(
                    userId = 1,
                    creationDate = "2025-10-31",
                    totalPrice = 150000.0,
                    paymentMethod = "Nequi",
                    status = "Pendiente de pago"
                )
            )

            viewModel.allCarts.collect { carts ->
                Log.d("TestCartActivity", "Carts actuales: $carts")
            }

            // Buscar por ID
            viewModel.getById(1) { cart ->
                Log.d("TestCartActivity", "Carts con ID 1: $cart")
            }

            // Actualizar
            viewModel.update(
                Cart(
                    cartId = 3,
                    userId = 1,
                    creationDate = "2025-10-31",
                    totalPrice = 120000.0,
                    paymentMethod = "Nequi",
                    status = "Pagado"
                )
            )

            // Eliminar
            viewModel.delete(
                Cart(
                    cartId = 2,
                    userId = 1,
                    creationDate = "2025-10-31",
                    totalPrice = 150000.0,
                    paymentMethod = "Nequi",
                    status = "Pendiente de pago"
                )
            )
        }
    }
}