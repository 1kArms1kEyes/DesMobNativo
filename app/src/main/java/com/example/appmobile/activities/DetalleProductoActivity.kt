package com.example.appmobile.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.Product
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter

class DetalleProductoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }

    private lateinit var imgHeader: ImageView
    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDescription: TextView

    private lateinit var btnSizeS: MaterialButton
    private lateinit var btnSizeM: MaterialButton
    private lateinit var btnSizeL: MaterialButton
    private lateinit var btnSizeXL: MaterialButton
    private lateinit var btnSize2XL: MaterialButton

    private lateinit var actvColor: MaterialAutoCompleteTextView

    private lateinit var btnDec: ImageButton
    private lateinit var btnInc: ImageButton
    private lateinit var tvQty: TextView

    private var currentProduct: Product? = null
    private var currentStock: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)

        imgHeader = findViewById(R.id.imgHeader)
        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        tvPrice = findViewById(R.id.tvPrice)
        tvDescription = findViewById(R.id.tvDescription)

        btnSizeS = findViewById(R.id.btnSizeS)
        btnSizeM = findViewById(R.id.btnSizeM)
        btnSizeL = findViewById(R.id.btnSizeL)
        btnSizeXL = findViewById(R.id.btnSizeXL)
        btnSize2XL = findViewById(R.id.btnSize2XL)

        actvColor = findViewById(R.id.actvColor)

        btnDec = findViewById(R.id.btnDec)
        btnInc = findViewById(R.id.btnInc)
        tvQty = findViewById(R.id.tvQty)

        btnBack.setOnClickListener { finish() }

        tvQty.text = "1"

        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        if (productId == -1) {
            Toast.makeText(this, "Producto inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val db = AppDatabase.getDatabase(applicationContext)
        val productDao = db.productDao()

        lifecycleScope.launch {
            val product = productDao.getProductById(productId)
            if (product == null) {
                Toast.makeText(
                    this@DetalleProductoActivity,
                    "No se encontró el producto",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            currentProduct = product
            currentStock = product.stock
            bindProductToUI(product)
            setupQuantityControls()
        }
    }

    private fun bindProductToUI(product: Product) {
        tvTitle.text = product.name
        tvPrice.text = "$${product.price}"
        tvDescription.text = product.description

        if (product.imageUrl.isNotBlank()) {
            val resId = resources.getIdentifier(product.imageUrl, "drawable", packageName)
            if (resId != 0) {
                imgHeader.setImageResource(resId)
            }
        }

        val colors = listOf(product.color)
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, colors)
        actvColor.setAdapter(colorAdapter)
        actvColor.setText(product.color, false)

        val allButtons = listOf(
            btnSizeS to "S",
            btnSizeM to "M",
            btnSizeL to "L",
            btnSizeXL to "XL",
            btnSize2XL to "2XL"
        )

        allButtons.forEach { (btn, _) ->
            btn.isEnabled = false
            btn.alpha = 0.4f
        }

        allButtons.firstOrNull { it.second.equals(product.size, ignoreCase = true) }?.let { (btn, _) ->
            btn.isEnabled = true
            btn.alpha = 1.0f
        }
    }

    private fun setupQuantityControls() {
        btnInc.setOnClickListener {
            val product = currentProduct ?: return@setOnClickListener
            val current = tvQty.text.toString().toIntOrNull() ?: 1

            if (current < product.stock) {
                tvQty.text = (current + 1).toString()
            } else {
                showStockAlert(product.stock)
            }
        }

        btnDec.setOnClickListener {
            val current = tvQty.text.toString().toIntOrNull() ?: 1
            if (current > 1) {
                tvQty.text = (current - 1).toString()
            }
        }
    }

    private fun showStockAlert(maxStock: Int) {
        AlertDialog.Builder(this)
            .setTitle("Stock insuficiente")
            .setMessage("Solo hay $maxStock unidades disponibles de este producto.")
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
