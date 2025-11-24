package com.example.appmobile.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.appmobile.data.entities.User
import com.example.appmobile.data.repository.UserRepository

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insert(user: User) = viewModelScope.launch {
        repository.insert(user)
    }

    fun update(user: User) = viewModelScope.launch {
        repository.update(user)
    }

    fun delete(user: User) = viewModelScope.launch {
        repository.delete(user)
    }

    fun getById(id: Int, callback: (User?) -> Unit) = viewModelScope.launch {
        val user = repository.getById(id)
        callback(user)
    }

    fun getByMail(mail: String, callback: (User?) -> Unit) = viewModelScope.launch {
        val user = repository.getByMail(mail)
        callback(user)
    }

    /**
     * Registra un usuario si el correo no existe.
     * callback:
     *   success = true  -> usuario creado, user != null
     *   success = false -> errorMessage con la razón
     */
    fun registerUser(
        username: String,
        mail: String,
        password: String,
        phone: String,
        address: String,
        city: String,
        neighborhood: String,
        callback: (Boolean, String?, User?) -> Unit
    ) {
        viewModelScope.launch {
            val existing = repository.getByMail(mail)
            if (existing != null) {
                callback(false, "El correo ya se encuentra registrado.", null)
            } else {
                val newUser = User(
                    username = username,
                    mail = mail,
                    password = password,
                    phone = phone,
                    address = address,
                    city = city,
                    neighborhood = neighborhood
                )
                repository.insert(newUser)
                val created = repository.getByMail(mail)
                callback(true, null, created)
            }
        }
    }

    fun login(username: String, password: String): LiveData<User?> {
        val result = MutableLiveData<User?>()
        viewModelScope.launch {
            val user = repository.login(username, password)
            result.postValue(user)
        }
        return result
    }
}

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
