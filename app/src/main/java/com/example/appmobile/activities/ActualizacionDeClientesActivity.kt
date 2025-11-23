package com.example.appmobile.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

class ActualizacionDeClientesActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actualizacion_de_clientes)

        // Edge-to-edge on the root view
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- ViewModel & session ---
        val db = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(db.userDao())
        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(userRepository)
        )[UserViewModel::class.java]

        sessionManager = SessionManager(this)

        // --- UI references ---
        val backContainer = findViewById<FrameLayout>(R.id.backContainer)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        val etUser = findViewById<TextInputEditText>(R.id.etUser)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val actvDept = findViewById<MaterialAutoCompleteTextView>(R.id.actvDept)
        val actvCity = findViewById<MaterialAutoCompleteTextView>(R.id.actvCity)
        val etAddress = findViewById<TextInputEditText>(R.id.etAddress)
        val btnUpdate = findViewById<MaterialButton>(R.id.btnUpdate)

        // Back behaviour
        backContainer.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

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

        // When a department is selected, load its municipalities
        actvDept.setOnItemClickListener { parent, _, position, _ ->
            val selectedDeptName = parent.getItemAtPosition(position) as String

            // Clear previously selected city
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

        // If the department is cleared, clear the city as well
        actvDept.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && actvDept.text.isNullOrBlank()) {
                actvCity.text = null
                actvCity.setAdapter(null)
            }
        }

        // ============================
        //   LOAD CURRENT USER DATA
        // ============================

        val currentUserId = sessionManager.getUserId()
        if (currentUserId == -1) {
            Toast.makeText(
                this,
                "No se encontró un usuario en sesión para actualizar.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            // Get user from DB and pre-fill the form
            viewModel.getById(currentUserId) { user ->
                if (user != null) {
                    runOnUiThread {
                        etUser.setText(user.username)
                        etEmail.setText(user.mail)
                        etPassword.setText(user.password)
                        etPhone.setText(user.phone)
                        etAddress.setText(user.address)

                        // Try to infer the department from the saved city
                        val departmentForCity = departments.firstOrNull { dept ->
                            dept.municipalities.any { it.equals(user.city, ignoreCase = true) }
                        }

                        if (departmentForCity != null) {
                            actvDept.setText(departmentForCity.name, false)
                            val cityAdapter = ArrayAdapter(
                                this,
                                android.R.layout.simple_list_item_1,
                                departmentForCity.municipalities
                            )
                            actvCity.setAdapter(cityAdapter)
                            actvCity.setText(user.city, false)
                        } else {
                            // Fallback: only show the city name if we can't infer the department
                            actvCity.setText(user.city, false)
                        }
                    }
                }
            }
        }

        // ============================
        //     UPDATE BUTTON LOGIC
        // ============================

        btnUpdate.setOnClickListener {
            val username = etUser.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString()?.trim().orEmpty()
            val mail = etEmail.text?.toString()?.trim().orEmpty()
            val phone = etPhone.text?.toString()?.trim().orEmpty()
            val city = actvCity.text?.toString()?.trim().orEmpty()
            val address = etAddress.text?.toString()?.trim().orEmpty()

            if (username.isEmpty() ||
                password.isEmpty() ||
                mail.isEmpty() ||
                phone.isEmpty() ||
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

            val userId = sessionManager.getUserId()
            if (userId == -1) {
                Toast.makeText(
                    this,
                    "No se encontró un usuario en sesión para actualizar.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Get current user, update its fields and save to DB
            viewModel.getById(userId) { user ->
                if (user == null) {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "No se pudo cargar la información del usuario.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val updatedUser = user.copy(
                        username = username,
                        mail = mail,
                        password = password,
                        phone = phone,
                        address = address,
                        city = city
                        // neighborhood is kept as is
                    )

                    // Persist in DB
                    viewModel.update(updatedUser)

                    // Update session with the new data
                    sessionManager.saveUser(updatedUser)

                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Datos actualizados correctamente.",
                            Toast.LENGTH_LONG
                        ).show()
                        // Go back to the previous screen (e.g., profile)
                        finish()
                    }
                }
            }
        }
    }
}
