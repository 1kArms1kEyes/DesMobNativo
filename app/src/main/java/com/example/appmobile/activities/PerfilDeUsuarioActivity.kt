package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmobile.R

import com.example.appmobile.activities.ListaDeProductoActivity

class PerfilDeUsuarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_de_usuario)

        val btnProductList = findViewById<Button>(R.id.btnProductList)

        btnProductList.setOnClickListener {
            val intent = Intent(this, ListaDeProductoActivity::class.java)
            startActivity(intent)
        }
    }
}