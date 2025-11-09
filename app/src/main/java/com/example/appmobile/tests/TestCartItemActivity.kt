package com.example.appmobile.tests

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.CartItem
import com.example.appmobile.data.repository.CartItemRepository
import com.example.appmobile.ui.viewmodels.CartItemViewModel
import com.example.appmobile.ui.viewmodels.CartItemViewModelFactory

class TestCartItemActivity : AppCompatActivity() {

    private val viewModel: CartItemViewModel by viewModels {
        CartItemViewModelFactory(
            CartItemRepository(
                AppDatabase.getDatabase(applicationContext).cartItemDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TestCartItemActivity", "Iniciando pruebas de Cart Item...")

        // Crear CartItem
        lifecycleScope.launch {
            viewModel.insert(
                CartItem(
                    productId = 1,
                    cartId = 1,
                    cartItemQuantity = 3
                )
            )
            viewModel.insert(
                CartItem(
                    productId = 2,
                    cartId = 2,
                    cartItemQuantity = 8
                )
            )
            viewModel.insert(
                CartItem(
                    productId = 3,
                    cartId = 3,
                    cartItemQuantity = 10
                )
            )

            viewModel.allCartItems.collect { cartItems ->
                Log.d("TestCartItemActivity", "Cart Items actuales: $cartItems")
            }

            // Buscar por ID
            viewModel.getById(1) { cartItem ->
                Log.d("TestCartItemActivity", "Cart Items con ID 1: $cartItem")
            }

            // Actualizar
            viewModel.update(
                CartItem(
                    cartItemId = 2,
                    productId = 2,
                    cartId = 3,
                    cartItemQuantity = 7
                )
            )

            // Eliminar
            viewModel.delete(
                CartItem(
                    cartItemId = 3,
                    productId = 3,
                    cartId = 3,
                    cartItemQuantity = 10
                )
            )
        }
    }
}