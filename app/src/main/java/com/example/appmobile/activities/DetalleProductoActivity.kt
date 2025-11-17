package com.example.appmobile.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.appmobile.R
import com.example.appmobile.data.dao.CartDao
import com.example.appmobile.data.dao.CartItemDao
import com.example.appmobile.data.dao.ProductDao
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.Cart
import com.example.appmobile.data.entities.CartItem
import com.example.appmobile.data.entities.Product
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleProductoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }

    // Views
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

    // Data
    private lateinit var productDao: ProductDao
    private lateinit var cartDao: CartDao
    private lateinit var cartItemDao: CartItemDao
    private var variants: List<Product> = emptyList()
    private var currentProduct: Product? = null
    private var currentSizeKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)

        // --- DB ---
        val db = AppDatabase.getDatabase(applicationContext)
        productDao = db.productDao()
        cartDao = db.cartDao()
        cartItemDao = db.cartItemDao()

        // --- Bind views ---
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

        tvQty.text = "1"

        // Botón "Agregar al carrito".
        // IMPORTANT: this assumes the button in the layout has id "btnUpdate".
        // If your XML uses a different id (e.g. btnAgregarCarrito), just change it here.
        val btnAgregarCarrito = findViewById<Button>(R.id.btnUpdate)
        btnAgregarCarrito.setOnClickListener {
            val product = currentProduct
            if (product == null) {
                Toast.makeText(this, "No se encontró el producto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = tvQty.text.toString().toIntOrNull() ?: 1
            if (quantity <= 0) {
                Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar stock disponible
            if (quantity > product.stock) {
                showStockAlert(product.stock)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // TODO: reemplazar con el id del usuario autenticado si ya lo manejas.
                val currentUserId = 1

                // 1) Crear carrito
                val cart = Cart(
                    userId = currentUserId,
                    creationDate = getCurrentDateString(),
                    totalPrice = product.price * quantity,
                    paymentMethod = "Por definir",
                    status = "Pendiente de pago"
                )

                // 2) Insertar carrito y obtener el id generado
                val cartId = cartDao.insertCart(cart).toInt()

                // 3) Crear item del carrito
                val cartItem = CartItem(
                    productId = product.productId,
                    cartId = cartId,
                    cartItemQuantity = quantity
                )

                // 4) Insertar item
                cartItemDao.insertCartItem(cartItem)

                // 5) Abrir pantalla del carrito
                val intent = Intent(this@DetalleProductoActivity, CarritoActivity::class.java)
                startActivity(intent)
            }
        }

        btnBack.setOnClickListener { finish() }

        // --- Cargar producto base y variantes ---
        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        if (productId == -1) {
            Toast.makeText(this, "Producto inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            val baseProduct = productDao.getProductById(productId)
            if (baseProduct == null) {
                Toast.makeText(
                    this@DetalleProductoActivity,
                    "No se encontró el producto",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            // Todas las variantes con el mismo nombre
            val allWithSameName = productDao.getProductsByName(baseProduct.name)
            variants = allWithSameName.filter { it.isActive }.ifEmpty { listOf(baseProduct) }

            currentProduct = variants.firstOrNull { it.productId == baseProduct.productId } ?: baseProduct

            currentProduct?.let { bindProductMainInfo(it) }

            setupSizeButtons()
            setupColorDropdown()
            setupQuantityControls()
        }
    }

    // Mostrar info básica del producto actual
    private fun bindProductMainInfo(product: Product) {
        tvTitle.text = product.name
        tvPrice.text = String.format("$%,.2f", product.price)
        tvDescription.text = product.description

        // Cargar drawable por nombre si imageUrl es un nombre de recurso; si no, usar default
        if (product.imageUrl.isNotBlank() && !product.imageUrl.startsWith("http")) {
            val resId = resources.getIdentifier(product.imageUrl, "drawable", packageName)
            if (resId != 0) {
                imgHeader.setImageResource(resId)
            } else {
                imgHeader.setImageResource(R.drawable.sample_product_large)
            }
        } else {
            imgHeader.setImageResource(R.drawable.sample_product_large)
        }
    }

    // Normalizar variaciones de talla a claves consistentes
    private fun normalizeSize(size: String): String {
        val normalized = size.trim().uppercase(Locale.getDefault())
        return when {
            normalized.startsWith("S") -> "S"
            normalized.startsWith("M") -> "M"
            normalized.startsWith("L") && !normalized.contains("X") -> "L"
            normalized.startsWith("XL") && !normalized.contains("2") -> "XL"
            normalized.contains("2XL") || normalized.contains("XXL") -> "2XL"
            else -> normalized
        }
    }

    private fun setupSizeButtons() {
        val sizeButtons: Map<String, MaterialButton> = mapOf(
            "S" to btnSizeS,
            "M" to btnSizeM,
            "L" to btnSizeL,
            "XL" to btnSizeXL,
            "2XL" to btnSize2XL
        )

        // Deshabilitar y resetear todo primero
        sizeButtons.forEach { (_, btn) ->
            btn.isEnabled = false
            btn.setOnClickListener(null)
        }

        val availableSizeKeys: Set<String> = variants
            .map { normalizeSize(it.size) }
            .toSet()

        // Habilitar sólo los que existan
        sizeButtons.forEach { (key, btn) ->
            val hasThisSize = availableSizeKeys.contains(key)
            btn.isEnabled = hasThisSize
            applySizeButtonStyle(btn, selected = false, enabled = hasThisSize)
            if (hasThisSize) {
                btn.setOnClickListener {
                    onSizeSelected(key)
                }
            }
        }

        // Talla seleccionada inicialmente
        val initialSizeKey = currentProduct?.let { normalizeSize(it.size) }
        val selectedKey = when {
            initialSizeKey != null && availableSizeKeys.contains(initialSizeKey) -> initialSizeKey
            availableSizeKeys.isNotEmpty() -> availableSizeKeys.first()
            else -> null
        }

        if (selectedKey != null) {
            currentSizeKey = selectedKey
            highlightSelectedSize(sizeButtons, selectedKey)
        }
    }

    private fun applySizeButtonStyle(
        button: MaterialButton,
        selected: Boolean,
        enabled: Boolean
    ) {
        val context = button.context

        val bgRes = when {
            !enabled -> R.color.gray
            selected -> R.color.black
            else -> R.color.white
        }
        val textColorRes = when {
            !enabled -> R.color.white
            selected -> R.color.white
            else -> R.color.black
        }

        val bgColor = ContextCompat.getColor(context, bgRes)
        val textColor = ContextCompat.getColor(context, textColorRes)

        button.backgroundTintList = ColorStateList.valueOf(bgColor)
        button.setTextColor(textColor)
    }

    private fun highlightSelectedSize(
        sizeButtons: Map<String, MaterialButton>,
        selectedKey: String
    ) {
        sizeButtons.forEach { (key, btn) ->
            val isSelected = key == selectedKey && btn.isEnabled
            applySizeButtonStyle(btn, selected = isSelected, enabled = btn.isEnabled)
        }

        // Actualizar producto y colores según talla seleccionada
        updateCurrentProductForSize()
        setupColorDropdown()
    }

    private fun onSizeSelected(sizeKey: String) {
        currentSizeKey = sizeKey
        val sizeButtons = mapOf(
            "S" to btnSizeS,
            "M" to btnSizeM,
            "L" to btnSizeL,
            "XL" to btnSizeXL,
            "2XL" to btnSize2XL
        )
        highlightSelectedSize(sizeButtons, sizeKey)
    }

    private fun updateCurrentProductForSize(selectedColor: String? = null) {
        val sizeKey = currentSizeKey ?: return

        val variantsForSize = variants.filter {
            normalizeSize(it.size) == sizeKey
        }

        if (variantsForSize.isEmpty()) return

        val product = if (selectedColor != null) {
            variantsForSize.firstOrNull { it.color == selectedColor } ?: variantsForSize.first()
        } else {
            val currentColor = currentProduct?.color
            if (currentColor != null) {
                variantsForSize.firstOrNull { it.color == currentColor } ?: variantsForSize.first()
            } else {
                variantsForSize.first()
            }
        }

        currentProduct = product
        bindProductMainInfo(product)
    }

    // Dropdown de color depende de la talla actual
    private fun setupColorDropdown() {
        val sizeKey = currentSizeKey ?: return

        val variantsForSize = variants.filter {
            normalizeSize(it.size) == sizeKey
        }

        val colors = variantsForSize.map { it.color }.distinct()
        if (colors.isEmpty()) {
            actvColor.setText("", false)
            actvColor.setAdapter(null)
            return
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            colors
        )
        actvColor.setAdapter(adapter)

        val currentColor = currentProduct?.color
        val initialColor = if (currentColor != null && colors.contains(currentColor)) {
            currentColor
        } else {
            colors.first()
        }

        actvColor.setText(initialColor, false)

        actvColor.setOnItemClickListener { _, _, position, _ ->
            val selectedColor = colors[position]
            updateCurrentProductForSize(selectedColor)
        }
    }

    // Cantidad limitada por stock
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

    private fun getCurrentDateString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun showStockAlert(maxStock: Int) {
        AlertDialog.Builder(this)
            .setTitle("Stock insuficiente")
            .setMessage("Solo hay $maxStock unidades disponibles de este producto.")
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
