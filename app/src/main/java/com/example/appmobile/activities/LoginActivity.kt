package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.repository.UserRepository
import com.example.appmobile.session.SessionManager
import com.example.appmobile.ui.viewmodels.UserViewModel
import com.example.appmobile.ui.viewmodels.UserViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // If session is already active, skip this screen
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, PerfilDeUsuarioActivity::class.java))
            finish()
            return
        }

        val db = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(db.userDao())
        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(userRepository)
        )[UserViewModel::class.java]

        val edtUser = findViewById<EditText>(R.id.etUser)
        val edtPass = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvForgot = findViewById<TextView>(R.id.tvForgot)

        // Forgot password → go to OlvidarContraseniaActivity
        tvForgot.setOnClickListener {
            startActivity(Intent(this, OlvidarContraseniaActivity::class.java))
        }

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
                    Toast.makeText(
                        this,
                        "Bienvenido ${user.username}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Save session
                    sessionManager.saveUser(user)

                    startActivity(Intent(this, PerfilDeUsuarioActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
