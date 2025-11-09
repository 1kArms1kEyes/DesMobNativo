package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmobile.R

import com.example.appmobile.activities.CreacionDeProductosActivity

class ListaDeProductoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_de_producto)

        val btnAdd = findViewById<ImageView>(R.id.btnAdd)

        btnAdd.setOnClickListener {
            val intent = Intent(this, CreacionDeProductosActivity::class.java)
            startActivity(intent)
        }
    }
}