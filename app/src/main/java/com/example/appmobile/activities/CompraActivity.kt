package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.Product
import com.example.appmobile.ui.viewmodels.adapters.CompraProductsAdapter
import kotlinx.coroutines.launch

class CompraActivity : AppCompatActivity() {

    private lateinit var adapter: CompraProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compra)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 1. Setup RecyclerView
        val rvCompra = findViewById<RecyclerView>(R.id.rvProducts)
        rvCompra.layoutManager = LinearLayoutManager(this)

        // 2. Create adapter with click callback to open DetalleProductoActivity
        adapter = CompraProductsAdapter { product ->
            val intent = Intent(this, DetalleProductoActivity::class.java)
            intent.putExtra(DetalleProductoActivity.EXTRA_PRODUCT_ID, product.productId)
            startActivity(intent)
        }
        rvCompra.adapter = adapter

        // 3. Get database
        val db = AppDatabase.getDatabase(applicationContext)
        val productDao = db.productDao()

        // 4. Load products from DB
        lifecycleScope.launch {
            productDao.getAllProducts().collect { products ->
                // 🔹 Group products by name, so we don't show duplicated cards
                val distinctByName: List<Product> = products
                    .filter { it.isActive } // optional: only show active products
                    .groupBy { it.name }
                    .map { (_, variants) ->
                        // Take the first variant to represent that product in the list
                        variants.first()
                    }

                adapter.submitList(distinctByName)
            }
        }

        // 5. Cart button opens CarritoActivity
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        btnCart.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            startActivity(intent)
        }
    }
}
