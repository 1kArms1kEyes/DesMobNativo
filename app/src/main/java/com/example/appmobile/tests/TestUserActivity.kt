package com.example.appmobile.tests

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.User
import com.example.appmobile.data.repository.UserRepository
import com.example.appmobile.ui.viewmodels.UserViewModel
import com.example.appmobile.ui.viewmodels.UserViewModelFactory

class TestUserActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(
            UserRepository(
                AppDatabase.getDatabase(applicationContext).userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TestUserActivity", "Iniciando pruebas de User...")

        // Crear usuario
        lifecycleScope.launch {
            viewModel.insert(
                User(
                    username = "paula",
                    mail = "paula@gmail.com",
                    password = "123456",
                    phone = "3000000000",
                    address = "Calle 123 #45-67",
                    city = "Bogotá",
                    neighborhood = "Usme"
                )
            )

            viewModel.allUsers.collect { users ->
                Log.d("TestUserActivity", "Usuarios actuales: $users")
            }

            viewModel.getById(1) { user ->
                Log.d("TestUserActivity", "Usuario con ID 1: $user")
            }

            // Actualizar
            viewModel.update(
                User(
                    userId = 1,
                    username = "Maria",
                    mail = "paula@gmail.com",
                    password = "123456",
                    phone = "3000000000",
                    address = "Calle 123 #45-67",
                    city = "Bogotá",
                    neighborhood = "Usme"
                )
            )

            // Eliminar
            viewModel.delete(
                User(
                    userId = 1,
                    username = "Maria",
                    mail = "paula@gmail.com",
                    password = "123456",
                    phone = "3000000000",
                    address = "Calle 123 #45-67",
                    city = "Bogotá",
                    neighborhood = "Usme"
                )
            )
        }
    }
}
