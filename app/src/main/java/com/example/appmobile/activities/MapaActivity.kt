package com.example.appmobile.activities

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.appmobile.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        // Views
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnAccept = findViewById<MaterialButton>(R.id.btnAccept)
        val btnCancel = findViewById<MaterialButton>(R.id.btnCancel)

        val tvEnterLocation = findViewById<TextView>(R.id.tvEnterLocation)
        val tilSelectedLocation = findViewById<TextInputLayout>(R.id.tilSelectedLocation)
        val tvSelectedLocation = findViewById<TextInputEditText>(R.id.tvSelectedLocation)
        val spinnerStores = findViewById<Spinner>(R.id.spinnerStores)

        val rbHome = findViewById<RadioButton>(R.id.rbHome)
        val rbStore = findViewById<RadioButton>(R.id.rbStore)
        val cardMap = findViewById<CardView>(R.id.cardMap)

        // Initial visibility
        tvEnterLocation.visibility = View.GONE
        tilSelectedLocation.visibility = View.GONE
        spinnerStores.visibility = View.GONE
        cardMap.visibility = View.GONE

        // Back and cancel
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        btnCancel.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // List of stores and coordinates (same as original)
        val stores = listOf(
            "Tienda Principal - Chapinero",
            "Tienda Norte - Usaquén",
            "Tienda Sur - Kennedy"
        )

        val storeLocations = mapOf(
            "Tienda Principal - Chapinero" to LatLng(4.649178, -74.062830),
            "Tienda Norte - Usaquén" to LatLng(4.702600, -74.042840),
            "Tienda Sur - Kennedy" to LatLng(4.612960, -74.145110)
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            stores
        )
        spinnerStores.adapter = adapter

        // Show / hide fields depending on selected radio
        val radioClickListener = View.OnClickListener {
            when (it.id) {
                R.id.rbHome -> {
                    tvEnterLocation.visibility = View.VISIBLE
                    tvEnterLocation.text = getString(R.string.ubicacion_label) // "Ingrese su ubicación"
                    tilSelectedLocation.visibility = View.VISIBLE
                    spinnerStores.visibility = View.GONE
                    cardMap.visibility = View.VISIBLE
                }
                R.id.rbStore -> {
                    tvEnterLocation.visibility = View.VISIBLE
                    tvEnterLocation.text = "Seleccione la tienda"
                    tilSelectedLocation.visibility = View.GONE
                    spinnerStores.visibility = View.VISIBLE
                    cardMap.visibility = View.VISIBLE
                }
            }
        }
        rbHome.setOnClickListener(radioClickListener)
        rbStore.setOnClickListener(radioClickListener)

        // When selecting a store, update the map
        spinnerStores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val storeName = stores[position]
                val coords = storeLocations[storeName]

                coords?.let {
                    if (::mMap.isInitialized) {
                        mMap.clear()
                        mMap.addMarker(MarkerOptions().position(it).title(storeName))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nothing
            }
        }

        // Search icon in the TextInputLayout: same behavior as original "Aceptar"
        tilSelectedLocation.setEndIconOnClickListener {
            val address = tvSelectedLocation.text?.toString().orEmpty()
            if (address.isNotBlank()) {
                searchLocation(address)
            } else {
                Toast.makeText(this, "Ingrese una dirección.", Toast.LENGTH_SHORT).show()
            }
        }

        // ACEPTAR button: send info to CarritoActivity
        btnAccept.setOnClickListener {
            when {
                rbHome.isChecked -> {
                    val address = tvSelectedLocation.text?.toString().orEmpty()
                    if (address.isBlank()) {
                        Toast.makeText(this, "Ingrese una dirección.", Toast.LENGTH_SHORT).show()
                    } else {
                        goToCart(address)
                    }
                }
                rbStore.isChecked -> {
                    val storeName = spinnerStores.selectedItem as? String
                    if (storeName.isNullOrBlank()) {
                        Toast.makeText(this, "Seleccione una tienda.", Toast.LENGTH_SHORT).show()
                    } else {
                        goToCart(storeName)
                    }
                }
                else -> {
                    Toast.makeText(
                        this,
                        "Seleccione si es envío a domicilio o recoger en tienda.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Initialize map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun goToCart(addressOrStore: String) {
        val intent = Intent(this, CarritoActivity::class.java).apply {
            putExtra("selected_address", addressOrStore)
        }
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val city = LatLng(4.598145, -74.076092)
        mMap.addMarker(MarkerOptions().position(city).title("Bogota"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(city))
    }

    private fun searchLocation(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val results = geocoder.getFromLocationName(address, 1)

            if (results != null && results.isNotEmpty()) {
                val location = results[0]
                val latLng = LatLng(location.latitude, location.longitude)

                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latLng).title(address))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

            } else {
                Toast.makeText(this, "No se encontró la ubicación.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
