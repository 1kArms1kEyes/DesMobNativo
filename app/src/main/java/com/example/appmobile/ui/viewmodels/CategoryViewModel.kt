package com.example.appmobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.appmobile.data.entities.Category
import com.example.appmobile.data.repository.CategoryRepository

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {
    val allCategories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }

    fun update(category: Category) = viewModelScope.launch {
        repository.update(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        repository.delete(category)
    }

    fun getById(id: Int, callback: (Category?) -> Unit) = viewModelScope.launch {
        val category = repository.getById(id)
        callback(category)
    }
}

@Suppress("UNCHECKED_CAST")
class CategoryViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
