package com.example.appmobile.activities

import android.content.Intent
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
import com.example.appmobile.data.entities.CartItemDetail
import com.example.appmobile.session.SessionManager
import com.example.appmobile.ui.viewmodels.adapters.CartItemsAdapter
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class CarritoActivity : AppCompatActivity(),
    CartItemsAdapter.OnCartItemInteractionListener {

    private lateinit var rvCart: RecyclerView
    private lateinit var cartAdapter: CartItemsAdapter
    private lateinit var cartItemDao: CartItemDao

    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvShipping: TextView

    // Dirección de envío (solo calle/dirección principal)
    private lateinit var tvStreet: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // 1. Views
        rvCart = findViewById(R.id.rvCart)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotal = findViewById(R.id.tvTotal)
        tvShipping = findViewById(R.id.tvShipping)

        tvStreet = findViewById(R.id.tvStreet)

        // Session & DB
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()
        val db = AppDatabase.getDatabase(this)
        cartItemDao = db.cartItemDao()
        val userDao = db.userDao()

        // Dirección que viene del mapa (si existe)
        val selectedAddressFromIntent = intent.getStringExtra("selected_address")

        // Cargar / actualizar dirección de envío desde/en la BD
        lifecycleScope.launch {
            val user = if (userId != -1) userDao.getUserById(userId) else null

            when {
                // Si venimos del mapa con una nueva dirección y hay usuario logueado:
                selectedAddressFromIntent != null && user != null -> {
                    val updatedUser = user.copy(address = selectedAddressFromIntent)
                    userDao.updateUser(updatedUser)
                    sessionManager.saveUser(updatedUser)

                    // Mostrar la dirección actualizada
                    tvStreet.text = updatedUser.address
                }

                // Si NO hay nueva dirección pero sí usuario logueado: usar la de la BD
                user != null -> {
                    tvStreet.text = user.address
                }

                // Si no hay usuario en sesión pero sí viene algo desde el mapa:
                selectedAddressFromIntent != null -> {
                    tvStreet.text = selectedAddressFromIntent
                }

                // Caso extremo: ni usuario ni dirección → se mantiene lo que haya en el XML
            }
        }

        // Adapter con callbacks para modificar el carrito
        cartAdapter = CartItemsAdapter(this)

        // Botón back → SIEMPRE abrir CompraActivity
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, CompraActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botón mapa
        val btnIvMap = findViewById<ShapeableImageView>(R.id.ivMap)
        btnIvMap.setOnClickListener {
            val intent = Intent(this, MapaActivity::class.java)
            startActivity(intent)
        }

        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = cartAdapter

        // Escuchar cambios en los items del carrito para actualizar lista y totales
        lifecycleScope.launch {
            cartItemDao.getCartItemsWithProduct().collect { cartItems ->
                cartAdapter.submitList(cartItems)

                val subtotal = cartItems.sumOf { it.lineTotal }
                tvSubtotal.text = formatCurrency(subtotal)

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
     * Convierte un texto de moneda a Double, por ejemplo "$12.345,00" → 12345.0
     */
    private fun parseCurrencyToDouble(value: String): Double {
        val clean = value.replace(Regex("[^\\d.]"), "")
        if (clean.isBlank()) return 0.0
        return clean.toDoubleOrNull() ?: 0.0
    }

    /**
     * Formatea un Double como moneda con símbolo $ y dos decimales.
     */
    private fun formatCurrency(amount: Double): String {
        return String.format("$%,.2f", amount)
    }

    // Alerta de stock insuficiente
    private fun showStockAlert(maxStock: Int) {
        AlertDialog.Builder(this)
            .setTitle("Stock insuficiente")
            .setMessage("Solo hay $maxStock unidades disponibles de este producto.")
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
