package com.example.appmobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.appmobile.data.entities.CartItem
import com.example.appmobile.data.repository.CartItemRepository

class CartItemViewModel(private val repository: CartItemRepository) : ViewModel() {
    val allCartItems: StateFlow<List<CartItem>> = repository.allCartItems
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(cartItem: CartItem) = viewModelScope.launch {
        repository.insert(cartItem)
    }

    fun update(cartItem: CartItem) = viewModelScope.launch {
        repository.update(cartItem)
    }

    fun delete(cartItem: CartItem) = viewModelScope.launch {
        repository.delete(cartItem)
    }

    fun getById(id: Int, callback: (CartItem?) -> Unit) = viewModelScope.launch {
        val cartItem = repository.getById(id)
        callback(cartItem)
    }
}

@Suppress("UNCHECKED_CAST")
class CartItemViewModelFactory(private val repository: CartItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartItemViewModel::class.java)) {
            return CartItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
