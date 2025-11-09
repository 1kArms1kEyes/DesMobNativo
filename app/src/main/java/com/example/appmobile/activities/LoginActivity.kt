package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.appmobile.R

import com.example.appmobile.ui.viewmodels.UserViewModel
import com.example.appmobile.ui.viewmodels.UserViewModelFactory
import com.example.appmobile.activities.PerfilDeUsuarioActivity
import com.example.appmobile.data.repository.UserRepository
import com.example.appmobile.data.database.AppDatabase


class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val db = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(db.userDao())
        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(userRepository)
        )[UserViewModel::class.java]

        val edtUser = findViewById<EditText>(R.id.etUser)
        val edtPass = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = edtUser.text.toString().trim()
            val password = edtPass.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(
                    "Campos incompletos",
                    "Por favor, complete todos los campos."
                )
                return@setOnClickListener
            }

            viewModel.login(username, password).observe(this) { user ->
                if (user != null) {
                    Toast.makeText(this, "Bienvenido ${user.username}", Toast.LENGTH_LONG).show()

                    startActivity(Intent(this, PerfilDeUsuarioActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
