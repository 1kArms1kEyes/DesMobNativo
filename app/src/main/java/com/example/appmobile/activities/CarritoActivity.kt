package com.example.appmobile.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.dao.CartItemDao
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.CartItem
import com.example.appmobile.data.entities.CartItemDetail
import com.example.appmobile.ui.viewmodels.adapters.CartItemsAdapter
import kotlinx.coroutines.launch

class CarritoActivity : AppCompatActivity(),
    CartItemsAdapter.OnCartItemInteractionListener {

    private lateinit var rvCart: RecyclerView
    private lateinit var cartAdapter: CartItemsAdapter
    private lateinit var cartItemDao: CartItemDao

    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvShipping: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // 1. Views
        rvCart = findViewById(R.id.rvCart)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotal = findViewById(R.id.tvTotal)
        tvShipping = findViewById(R.id.tvShipping)

        // Adapter con callbacks para modificar el carrito
        cartAdapter = CartItemsAdapter(this)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // 2. Set up RecyclerView
        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = cartAdapter

        // 3. Get DAO from Room database
        val db = AppDatabase.getDatabase(this)
        cartItemDao = db.cartItemDao()

        // 4. Collect the Flow<List<CartItemDetail>> and feed the adapter + update totals
        lifecycleScope.launch {
            cartItemDao.getCartItemsWithProduct().collect { cartItems ->
                // Update list
                cartAdapter.submitList(cartItems)

                // ---- a) SUBTOTAL: sum of lineTotal (p x q) for each item ----
                val subtotal = cartItems.sumOf { it.lineTotal }
                tvSubtotal.text = formatCurrency(subtotal)

                // ---- b) TOTAL: subtotal + shipping ----
                val shippingValue = parseCurrencyToDouble(tvShipping.text.toString())
                val total = subtotal + shippingValue
                tvTotal.text = formatCurrency(total)
            }
        }
    }

    // + cantidad, respetando stock
    override fun onIncreaseQuantity(item: CartItemDetail) {
        lifecycleScope.launch {
            val entity = cartItemDao.getCartItemById(item.cartItemId) ?: return@launch
            val currentQty = entity.cartItemQuantity

            // No permitir superar el stock
            if (currentQty >= item.stock) {
                showStockAlert(item.stock)
            } else {
                val updated = entity.copy(
                    cartItemQuantity = currentQty + 1
                )
                cartItemDao.updateCartItem(updated)
            }
        }
    }

    // − cantidad: si baja de 1, eliminar el producto
    override fun onDecreaseQuantity(item: CartItemDetail) {
        lifecycleScope.launch {
            val entity = cartItemDao.getCartItemById(item.cartItemId) ?: return@launch
            val currentQty = entity.cartItemQuantity
            val newQty = currentQty - 1

            if (newQty <= 0) {
                // Mismo comportamiento que el botón eliminar
                cartItemDao.deleteCartItem(entity)
            } else {
                val updated = entity.copy(
                    cartItemQuantity = newQty
                )
                cartItemDao.updateCartItem(updated)
            }
        }
    }

    // Eliminar producto del carrito (botón de borrar)
    override fun onRemoveItem(item: CartItemDetail) {
        lifecycleScope.launch {
            val entity = cartItemDao.getCartItemById(item.cartItemId) ?: return@launch
            cartItemDao.deleteCartItem(entity)
        }
    }

    /**
     * Parses a currency string like "$1,234.56" into a Double = 1234.56
     * Removes everything that is not a digit or decimal point.
     */
    private fun parseCurrencyToDouble(value: String): Double {
        val clean = value.replace(Regex("[^\\d.]"), "")
        if (clean.isBlank()) return 0.0
        return try {
            clean.toDouble()
        } catch (_: NumberFormatException) {
            0.0
        }
    }

    /**
     * Formats a Double as a currency string with $ and two decimals.
     */
    private fun formatCurrency(amount: Double): String {
        return String.format("$%,.2f", amount)
    }

    // Alerta de stock insuficiente, igual a la de DetalleProductoActivity
    private fun showStockAlert(maxStock: Int) {
        AlertDialog.Builder(this)
            .setTitle("Stock insuficiente")
            .setMessage("Solo hay $maxStock unidades disponibles de este producto.")
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
