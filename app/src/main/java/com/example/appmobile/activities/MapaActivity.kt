package com.example.appmobile.activities

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.util.Locale

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.appmobile.R

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        // ---- Views ----
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnAccept = findViewById<MaterialButton>(R.id.btnAccept)
        val tvSelectedLocation = findViewById<TextInputEditText>(R.id.tvSelectedLocation)
        val spinnerStores = findViewById<Spinner>(R.id.spinnerStores)
        val rbHome = findViewById<RadioButton>(R.id.rbHome)
        val rbStore = findViewById<RadioButton>(R.id.rbStore)

        // ---- Botón atrás ----
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // ---- Lista de tiendas ----
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

        // ---- Mostrar/Ocultar campos según selección ----
        rbHome.setOnClickListener {
            tvSelectedLocation.visibility = View.VISIBLE
            spinnerStores.visibility = View.GONE
        }

        rbStore.setOnClickListener {
            tvSelectedLocation.visibility = View.GONE
            spinnerStores.visibility = View.VISIBLE
        }

        // ---- Cuando selecciona una tienda: actualizar mapa ----
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
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(it).title(storeName))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // ---- Botón Aceptar = solo funciona para DOMICILIO ----
        btnAccept.setOnClickListener {
            if (rbHome.isChecked) {
                val address = tvSelectedLocation.text.toString()
                if (address.isNotEmpty()) {
                    searchLocation(address)
                } else {
                    Toast.makeText(this, "Ingrese una dirección.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Seleccionaste recoger en tienda.", Toast.LENGTH_SHORT).show()
            }
        }

        // ---- Inicializar el mapa ----
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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