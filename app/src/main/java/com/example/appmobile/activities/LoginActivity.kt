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

        // Views from activity_login.xml
        val etUser = findViewById<TextInputEditText>(R.id.etUser)
        val etPass = findViewById<TextInputEditText>(R.id.etPass)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val tvForgot = findViewById<TextView>(R.id.tvForgot)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        val sessionManager = SessionManager(this)
        val db = AppDatabase.getDatabase(applicationContext)
        val userDao = db.userDao()

        // Back arrow
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // "Olvidé mi contraseña"
        tvForgot.setOnClickListener {
            startActivity(Intent(this, OlvidarContraseniaActivity::class.java))
        }

        // Ingresar button
        btnLogin.setOnClickListener {
            val userInput = etUser.text?.toString()?.trim().orEmpty()
            val password = etPass.text?.toString()?.trim().orEmpty()

            if (userInput.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario/correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // 1) Check if there are any users at all
                    val count = userDao.getUserCount()
                    if (count == 0) {
                        Toast.makeText(
                            this@LoginActivity,
                            "No hay usuarios registrados. Cree una cuenta primero.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    // 2) Try login by username
                    var user = userDao.login(userInput, password)

                    // 3) If that fails, try interpreting input as email
                    if (user == null) {
                        val byMail = userDao.getUserByMail(userInput)
                        if (byMail != null && byMail.password == password) {
                            user = byMail
                        }
                    }

                    if (user != null) {
                        // Successful login
                        sessionManager.saveUser(user)

                        Toast.makeText(
                            this@LoginActivity,
                            "Bienvenido, ${user.username}",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@LoginActivity,
                            PerfilDeUsuarioActivity::class.java
                        )
                        startActivity(intent)
                        finish() // do not come back to login with back button
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Usuario/correo o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error al iniciar sesión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
