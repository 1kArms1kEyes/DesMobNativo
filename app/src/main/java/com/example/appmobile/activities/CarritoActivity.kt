package com.example.appmobile.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.ui.viewmodels.adapters.CartItemsAdapter
import kotlinx.coroutines.launch

class CarritoActivity : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private val cartAdapter = CartItemsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // 1. Set up RecyclerView
        rvCart = findViewById(R.id.rvCart)
        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = cartAdapter

        // 2. Get DAO from Room database
        val db = AppDatabase.getDatabase(this)
        val cartItemDao = db.cartItemDao()

        // 3. Collect the Flow<List<CartItemDetail>> and feed the adapter
        lifecycleScope.launch {
            cartItemDao.getCartItemsWithProduct().collect { cartItems ->
                cartAdapter.submitList(cartItems)
            }
        }
    }
}
