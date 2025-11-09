package com.example.appmobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.appmobile.data.entities.Product
import com.example.appmobile.data.repository.ProductRepository

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(product: Product) = viewModelScope.launch {
        repository.insert(product)
    }

    fun update(product: Product) = viewModelScope.launch {
        repository.update(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        repository.delete(product)
    }

    fun getById(id: Int, callback: (Product?) -> Unit) = viewModelScope.launch {
        val product = repository.getById(id)
        callback(product)
    }
}

@Suppress("UNCHECKED_CAST")
class ProductViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
