package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.repository.UserRepository
import com.example.appmobile.ui.viewmodels.UserViewModel
import com.example.appmobile.ui.viewmodels.UserViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class OlvidarContraseniaActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel

    // Código hardcodeado para pruebas
    private var generatedCode: String? = null
    private var isCodeStage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_olvidar_contrasenia)

        // Edge-to-edge padding using the root "main" view of this layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(db.userDao())
        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(userRepository)
        )[UserViewModel::class.java]

        val tilCode = findViewById<TextInputLayout>(R.id.tilCode)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etCode = findViewById<TextInputEditText>(R.id.etCode)
        val btnCancel = findViewById<MaterialButton>(R.id.btnCancel)
        val btnReset = findViewById<MaterialButton>(R.id.btnReset)

        // Hidden at start
        tilCode.visibility = View.GONE

        btnCancel.setOnClickListener {
            finish()
        }

        btnReset.setOnClickListener {
            if (!isCodeStage) {
                // === PRIMERA FASE: usuario ingresa correo ===
                val email = etEmail.text?.toString()?.trim().orEmpty()

                if (email.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Por favor, ingrese su correo electrónico.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                // Mensaje largo en diálogo para que no se corte
                val baseMessage =
                    "Si su correo corresponde se encuentra registrado recibirá un correo electrónico para poder reemplazarla"

                // Código hardcodeado para pruebas
                val testCode = "ABC1234"
                generatedCode = testCode

                // Mostrar el diálogo con el mensaje + el código de prueba
                showInfoDialog(
                    "$baseMessage\n\nCódigo de prueba (solo para test): $testCode"
                )

                // Mostrar el campo de código inmediatamente
                tilCode.visibility = View.VISIBLE
                isCodeStage = true

                // (Opcional) seguir consultando la BD para el futuro,
                // pero para esta prueba el flujo no depende del resultado.
                viewModel.getByMail(email) { user ->
                    if (user == null) {
                        // Nada especial que hacer aquí para la prueba;
                        // el código ya está hardcodeado.
                    }
                }

            } else {
                // === SEGUNDA FASE: usuario ingresa código ===
                val inputCode = etCode.text?.toString()?.trim().orEmpty()

                if (inputCode.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Por favor, ingrese el código recibido.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                if (generatedCode == null) {
                    showInfoDialog(
                        "No se ha generado ningún código de recuperación. Vuelva a intentarlo."
                    )
                    return@setOnClickListener
                }

                if (inputCode == generatedCode) {
                    // Código correcto → ir a activity_actualizacion_de_clientes
                    startActivity(
                        Intent(
                            this,
                            ActualizacionDeClientesActivity::class.java
                        )
                    )
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Código incorrecto.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showInfoDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
