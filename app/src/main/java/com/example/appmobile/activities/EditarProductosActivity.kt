package com.example.appmobile.activities

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appmobile.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.Product
import com.example.appmobile.data.dao.ProductDao
import com.example.appmobile.ui.viewmodels.ProductViewModel
import com.example.appmobile.ui.viewmodels.ProductViewModelFactory
import com.bumptech.glide.Glide

class EditarProductosActivity : AppCompatActivity() {

    private lateinit var productDao: ProductDao
    private var currentProduct: Product? = null
    private var selectedImageUri: Uri? = null
    private var ivPreview: ImageView? = null


    private val allowedCategories = listOf("Hoodies", "Camisetas", "Snapbacks", "Tenis")
    private val allowedSizes = listOf("S", "M", "L", "XL", "2XL")
    private val allowedColors = listOf("Rojo", "Azul", "Verde")

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                ivPreview?.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_productos)

        val db = AppDatabase.getDatabase(applicationContext)
        productDao = db.productDao()


        val productId = intent.getIntExtra("product_id", -1)
        if (productId == -1) {
            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        val tilNombre = findViewById<TextInputLayout>(R.id.tilNombre)
        val tilCategoria = findViewById<TextInputLayout>(R.id.tilCategoria)
        val tilPrecio = findViewById<TextInputLayout>(R.id.tilPrecio)
        val tilDescripcion = findViewById<TextInputLayout>(R.id.tilDescripcion)
        val tilCantidad = findViewById<TextInputLayout>(R.id.tilCantidad)
        val tilTalla = findViewById<TextInputLayout>(R.id.tilTalla)
        val tilColor = findViewById<TextInputLayout>(R.id.tilColor)

        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)
        val etCategoria = findViewById<TextInputEditText>(R.id.etCategoria)
        val etPrecio = findViewById<TextInputEditText>(R.id.etPrecio)
        val etDescripcion = findViewById<TextInputEditText>(R.id.etDescripcion)
        val etCantidad = findViewById<TextInputEditText>(R.id.etCantidad)
        val etTalla = findViewById<TextInputEditText>(R.id.etTalla)
        val actvColor = findViewById<MaterialAutoCompleteTextView>(R.id.actvColor)

        ivPreview = findViewById(R.id.ivPreview)
        val btnSeleccionar = findViewById<MaterialButton>(R.id.btnSeleccionar)
        val btnSave = findViewById<MaterialButton>(R.id.btnSave)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val colorAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            allowedColors
        )
        actvColor.setAdapter(colorAdapter)

        btnSeleccionar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        lifecycleScope.launch {
            val product = productDao.getProductById(productId)
            if (product == null) {
                Toast.makeText(
                    this@EditarProductosActivity,
                    "Producto no encontrado en la base de datos",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }

            currentProduct = product

                val categoryName = when (product.categoryId) {
                    1 -> "Hoodies"
                    2 -> "Camisetas"
                    3 -> "Snapbacks"
                    4 -> "Tenis"
                    else -> ""
                }

                etNombre.setText(product.name)
                etCategoria.setText(categoryName)
                etPrecio.setText(product.price.toString())
                etDescripcion.setText(product.description)
                etCantidad.setText(product.stock.toString())
                etTalla.setText(product.size)
                actvColor.setText(product.color, false)

            if (product.imageUrl.isNotEmpty()) {
                if (product.imageUrl.startsWith("http://") || product.imageUrl.startsWith("https://")) {
                    // URL remota (por ejemplo, Google Drive)
                    ivPreview?.let { imageView ->
                        Glide.with(this@EditarProductosActivity)
                            .load(product.imageUrl)
                            .placeholder(R.drawable.sample_product_large)
                            .error(R.drawable.sample_product_large)
                            .into(imageView)
                    }
                } else {
                    // Uri local (content:// o file://) → se mantiene el comportamiento previo
                    val uri = Uri.parse(product.imageUrl)
                    selectedImageUri = uri
                    ivPreview?.setImageURI(uri)
                }
            }

            }
        btnSave.setOnClickListener {
            val updatedProduct = buildUpdatedProduct(
                productId,
                tilNombre, tilCategoria, tilPrecio, tilDescripcion,
                tilCantidad, tilTalla, tilColor,
                etNombre, etCategoria, etPrecio, etDescripcion,
                etCantidad, etTalla, actvColor
            )

            if (updatedProduct != null) {
                showConfirmUpdateDialog(updatedProduct)
            }
        }
    }

    private fun buildUpdatedProduct(
        productId: Int,
        tilNombre: TextInputLayout,
        tilCategoria: TextInputLayout,
        tilPrecio: TextInputLayout,
        tilDescripcion: TextInputLayout,
        tilCantidad: TextInputLayout,
        tilTalla: TextInputLayout,
        tilColor: TextInputLayout,
        etNombre: TextInputEditText,
        etCategoria: TextInputEditText,
        etPrecio: TextInputEditText,
        etDescripcion: TextInputEditText,
        etCantidad: TextInputEditText,
        etTalla: TextInputEditText,
        actvColor: MaterialAutoCompleteTextView
    ): Product? {
        tilNombre.error = null
        tilCategoria.error = null
        tilPrecio.error = null
        tilDescripcion.error = null
        tilCantidad.error = null
        tilTalla.error = null
        tilColor.error = null

        var isValid = true

        val nombre = etNombre.text?.toString()?.trim().orEmpty()
        val categoria = etCategoria.text?.toString()?.trim().orEmpty()
        val precioStr = etPrecio.text?.toString()?.trim().orEmpty()
        val descripcion = etDescripcion.text?.toString()?.trim().orEmpty()
        val cantidadStr = etCantidad.text?.toString()?.trim().orEmpty()
        val tallaInput = etTalla.text?.toString()?.trim().orEmpty()
        val colorInput = actvColor.text?.toString()?.trim().orEmpty()

        if (nombre.isEmpty()) {
            tilNombre.error = "El nombre es obligatorio"
            isValid = false
        }

        if (categoria.isEmpty()) {
            tilCategoria.error = "La categoría es obligatoria"
            isValid = false
        } else if (!allowedCategories.contains(categoria)) {
            tilCategoria.error = "Categoría inválida. Usa: ${allowedCategories.joinToString()}"
            isValid = false
        }

        val precio = precioStr.toDoubleOrNull()
        if (precio == null || precio <= 0.0) {
            tilPrecio.error = "Precio inválido"
            isValid = false
        }

        if (descripcion.isEmpty()) {
            tilDescripcion.error = "La descripción es obligatoria"
            isValid = false
        }

        val cantidad = cantidadStr.toIntOrNull()
        if (cantidad == null || cantidad < 0) {
            tilCantidad.error = "Cantidad inválida"
            isValid = false
        }

        val talla = tallaInput.uppercase()
        if (!allowedSizes.contains(talla)) {
            tilTalla.error = "Talla inválida. Usa: ${allowedSizes.joinToString()}"
            isValid = false
        }

        val color = colorInput.replaceFirstChar { it.uppercase() }
        if (!allowedColors.contains(color)) {
            tilColor.error = "Color inválido. Usa: ${allowedColors.joinToString()}"
            isValid = false
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Selecciona una imagen para el producto", Toast.LENGTH_SHORT)
                .show()
            isValid = false
        }

        if (!isValid) return null

        val categoryId = when (categoria) {
            "Hoodies" -> 1
            "Camisetas" -> 2
            "Snapbacks" -> 3
            "Tenis" -> 4
            else -> {
                tilCategoria.error = "Categoría inválida"
                return null
            }
        }

        val imageUrl = selectedImageUri.toString()
        val isActive = currentProduct?.isActive ?: true

        return Product(
            productId = productId,
            categoryId = categoryId,
            name = nombre,
            price = precio!!,
            description = descripcion,
            size = talla,
            stock = cantidad!!,
            color = color,
            imageUrl = imageUrl,
            isActive = isActive
        )
    }

    private fun showConfirmUpdateDialog(product: Product) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_action, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)
        val btnNo = dialogView.findViewById<MaterialButton>(R.id.btnNo)
        val btnSi = dialogView.findViewById<MaterialButton>(R.id.btnSi)

        tvTitle.text = "Confirmar cambios del producto"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnClose.setOnClickListener { dialog.dismiss() }
        btnNo.setOnClickListener { dialog.dismiss() }

        btnSi.setOnClickListener {
            lifecycleScope.launch {
                productDao.updateProduct(product)
                Toast.makeText(
                    this@EditarProductosActivity,
                    "Producto actualizado correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                // Cerramos este activity → se muestra el listado
                finish()
            }
        }

        dialog.show()
    }
}