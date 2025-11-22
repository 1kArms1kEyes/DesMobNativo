package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.appmobile.R
import com.example.appmobile.data.ColombiaLocations
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.repository.UserRepository
import com.example.appmobile.session.SessionManager
import com.example.appmobile.ui.viewmodels.UserViewModel
import com.example.appmobile.ui.viewmodels.UserViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class RegistroDeClientesActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_de_clientes)

        // Edge to edge (optional, uses whole root view)
        val rootView = findViewById<android.view.View>(R.id.backContainer).rootView
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- ViewModel & Session ---
        val db = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(db.userDao())
        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(userRepository)
        )[UserViewModel::class.java]

        sessionManager = SessionManager(this)

        // --- UI references ---
        val backContainer = findViewById<FrameLayout>(R.id.backContainer)

        val etUser = findViewById<TextInputEditText>(R.id.etUser)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val actvDept = findViewById<MaterialAutoCompleteTextView>(R.id.actvDept)
        val actvCity = findViewById<MaterialAutoCompleteTextView>(R.id.actvCity)
        val etAddress = findViewById<TextInputEditText>(R.id.etAddress)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)

        // --- Back button behavior ---
        backContainer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // ============================
        //   CASCADING DROPDOWNS
        // ============================

        // Load departments from JSON
        val departments = ColombiaLocations.getDepartments(this)
        val departmentNames = departments.map { it.name }

        // Adapter for departments
        val deptAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            departmentNames
        )
        actvDept.setAdapter(deptAdapter)

        // When department is selected → load municipalities for that department
        actvDept.setOnItemClickListener { parent, _, position, _ ->
            val selectedDeptName = parent.getItemAtPosition(position) as String

            // Clear any previously selected city
            actvCity.text = null

            val municipalities =
                ColombiaLocations.getMunicipalitiesForDepartment(this, selectedDeptName)

            val cityAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                municipalities
            )
            actvCity.setAdapter(cityAdapter)
        }

        // Optional: if department is cleared (loses focus empty) → clear city
        actvDept.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && actvDept.text.isNullOrBlank()) {
                actvCity.text = null
                actvCity.setAdapter(null)
            }
        }

        // ============================
        //   REGISTER BUTTON LOGIC
        // ============================

        btnRegister.setOnClickListener {
            val username = etUser.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString()?.trim().orEmpty()
            val mail = etEmail.text?.toString()?.trim().orEmpty()
            val phone = etPhone.text?.toString()?.trim().orEmpty()
            val dept = actvDept.text?.toString()?.trim().orEmpty()
            val city = actvCity.text?.toString()?.trim().orEmpty()
            val address = etAddress.text?.toString()?.trim().orEmpty()

            // Simple validation
            if (username.isEmpty() ||
                password.isEmpty() ||
                mail.isEmpty() ||
                phone.isEmpty() ||
                dept.isEmpty() ||
                city.isEmpty() ||
                address.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Por favor, complete todos los campos obligatorios.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // If you don't have neighborhood yet, store empty string
            val neighborhood = ""

            // Use the helper in UserViewModel we defined earlier
            viewModel.registerUser(
                username = username,
                mail = mail,
                password = password,
                phone = phone,
                address = address,
                city = city,
                neighborhood = neighborhood
            ) { success, errorMessage, createdUser ->
                runOnUiThread {
                    if (!success) {
                        val message = errorMessage ?: "No se pudo registrar el usuario."
                        AlertDialog.Builder(this)
                            .setTitle("Registro")
                            .setMessage(message)
                            .setPositiveButton("Aceptar", null)
                            .show()
                    } else if (createdUser != null) {
                        // Save user in session
                        sessionManager.saveUser(createdUser)

                        // Go to profile
                        val intent = Intent(this, PerfilDeUsuarioActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Ocurrió un error inesperado al crear el usuario.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}
