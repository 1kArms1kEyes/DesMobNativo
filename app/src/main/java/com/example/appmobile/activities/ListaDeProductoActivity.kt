package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.ui.viewmodels.adapters.AdminProductsAdapter
import kotlinx.coroutines.launch

class ListaDeProductoActivity : AppCompatActivity() {

    // Adapter that will control how products are shown in the RecyclerView
    private lateinit var productsAdapter: AdminProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // You had this commented out before, we can leave it like that if you prefer:
        // enableEdgeToEdge()
        setContentView(R.layout.activity_lista_de_producto)

        // --- "Add" button (top right circle) ---
        val btnAdd = findViewById<ImageView>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(this, CreacionDeProductosActivity::class.java)
            startActivity(intent)
        }

        // --- Optional: Back button (top left circle) ---
        // This matches the ImageButton with id btnBack in your XML
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            // Close this screen and return to the previous one
            finish()
        }

        // --- 1. Setup RecyclerView ---
        val rvAdminProducts = findViewById<RecyclerView>(R.id.rvAdminProducts)
        rvAdminProducts.layoutManager = LinearLayoutManager(this)
        productsAdapter = AdminProductsAdapter()
        rvAdminProducts.adapter = productsAdapter

        // --- 2. Get a reference to the Room database and ProductDao ---
        val db = AppDatabase.getDatabase(applicationContext)
        val productDao = db.productDao()

        // --- 3. Collect products from the database and send them to the adapter ---
        lifecycleScope.launch {
            // getAllProducts() returns a Flow<List<Product>>
            productDao.getAllProducts().collect { products ->
                // This updates the RecyclerView with the latest products
                productsAdapter.submitList(products)
            }
        }
    }
}
