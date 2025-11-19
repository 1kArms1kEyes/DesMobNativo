package com.example.appmobile.activities;

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.imageview.ShapeableImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.appmobile.R

import com.example.appmobile.ui.viewmodels.CartViewModel

class CarritoDeComprasActivity : AppCompatActivity() {
    private lateinit var cartViewModel: CartViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carrito)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnIvMap = findViewById<ShapeableImageView>(R.id.ivMap)
        btnIvMap.setOnClickListener {
            val intent = Intent(this, MapaActivity::class.java)
            startActivity(intent)
        }

    }
}
