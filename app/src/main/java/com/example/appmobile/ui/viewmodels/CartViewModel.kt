package com.example.appmobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.appmobile.data.entities.Cart
import com.example.appmobile.data.repository.CartRepository

class CartViewModel(private val repository: CartRepository) : ViewModel() {
    val allCarts: StateFlow<List<Cart>> = repository.allCarts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(cart: Cart) = viewModelScope.launch {
        repository.insert(cart)
    }

    fun update(cart: Cart) = viewModelScope.launch {
        repository.update(cart)
    }

    fun delete(cart: Cart) = viewModelScope.launch {
        repository.delete(cart)
    }

    fun getById(id: Int, callback: (Cart?) -> Unit) = viewModelScope.launch {
        val cart = repository.getById(id)
        callback(cart)
    }
}

@Suppress("UNCHECKED_CAST")
class CartViewModelFactory(private val repository: CartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
