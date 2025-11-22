package com.example.appmobile.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmobile.R
import com.example.appmobile.data.ColombiaLocations
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class ActualizacionDeClientesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_actualizacion_de_clientes)

        // Use the root content view instead of R.id.main (which doesn't exist in this layout)
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // === Cascading departamento / municipio ===
        val actvDept = findViewById<MaterialAutoCompleteTextView>(R.id.actvDept)
        val actvCity = findViewById<MaterialAutoCompleteTextView>(R.id.actvCity)

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

        // If you already have a saved department for this user,
        // you can pre-select it here, e.g.:
        // actvDept.setText(savedDepartmentName, false)
        // and then pre-load its municipalities.

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

        actvDept.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && actvDept.text.isNullOrBlank()) {
                actvCity.text = null
                actvCity.setAdapter(null)
            }
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}
