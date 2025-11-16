package com.example.appmobile.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.ui.viewmodels.adapters.CompraProductsAdapter
import android.content.Intent
import android.widget.ImageButton
import com.example.appmobile.activities.CarritoActivity
import kotlinx.coroutines.launch

class CompraActivity : AppCompatActivity() {

    private lateinit var adapter: CompraProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compra)

        // 1. Setup RecyclerView
        val rvCompra = findViewById<RecyclerView>(R.id.rvProducts)
        rvCompra.layoutManager = LinearLayoutManager(this)

        adapter = CompraProductsAdapter()
        rvCompra.adapter = adapter

        // 2. Get database
        val db = AppDatabase.getDatabase(applicationContext)
        val productDao = db.productDao()

        // 3. Load products from DB
        lifecycleScope.launch {
            productDao.getAllProducts().collect { products ->
                adapter.submitList(products)
            }
        }

        // 4. Cart button opens CarritoActivity
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        btnCart.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            startActivity(intent)
        }
    }
}
