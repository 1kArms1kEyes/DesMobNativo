package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.session.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val etUser = findViewById<TextInputEditText>(R.id.etUser)
        val etPass = findViewById<TextInputEditText>(R.id.etPass)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvForgot = findViewById<TextView>(R.id.tvForgot)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        val sessionManager = SessionManager(this)
        val db = AppDatabase.getDatabase(applicationContext)
        val userDao = db.userDao()

        // Back arrow → go back
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // "Olvidar contraseña" link
        tvForgot.setOnClickListener {
            startActivity(Intent(this, OlvidarContraseniaActivity::class.java))
        }

        // Login button
        btnLogin.setOnClickListener {
            val username = etUser.text?.toString()?.trim().orEmpty()
            val password = etPass.text?.toString()?.trim().orEmpty()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userDao.login(username, password)

                if (user != null) {
                    // Save session
                    sessionManager.saveUser(user)

                    // Go to profile
                    val intent = Intent(this@LoginActivity, PerfilDeUsuarioActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
