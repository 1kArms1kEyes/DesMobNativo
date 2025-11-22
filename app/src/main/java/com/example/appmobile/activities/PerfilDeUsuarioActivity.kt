package com.example.appmobile.activities

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.CartSummary
import com.example.appmobile.session.SessionManager
import com.example.appmobile.ui.viewmodels.adapters.OrdersAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import com.google.android.material.button.MaterialButton


class PerfilDeUsuarioActivity : AppCompatActivity() {

    // Adapter for the RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter

    // Full list of orders for search
    private var allOrders: List<CartSummary> = emptyList()

    // Views for the avatar and camera icon
    private lateinit var imgAvatar: ShapeableImageView
    private lateinit var ivCamera: ImageView

    // Temporary URI where the camera will save the photo
    private var tempPhotoUri: Uri? = null

    // Launcher to open the image picker (gallery) and get the selected image URI
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imgAvatar.setImageURI(uri)
                // Aquí podrías persistir el URI en SharedPreferences / DB si lo necesitas
            }
        }

    // Launcher to take a picture with the camera and save it to the temporary URI
    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success && tempPhotoUri != null) {
                imgAvatar.setImageURI(tempPhotoUri)
            }
        }

    // Launcher to request camera permission at runtime
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_de_usuario)

        // --- Avatar and camera setup ---
        imgAvatar = findViewById(R.id.imgAvatar)
        ivCamera = findViewById(R.id.ivCamera)

        ivCamera.setOnClickListener {
            showImageSourceDialog()
        }

        // --- Session + logout button ---
        val sessionManager = SessionManager(this)

        // Mostrar el nombre del usuario en el encabezado, si existe en la sesión
        val tvName = findViewById<TextView>(R.id.tvName)
        sessionManager.getUsername()?.let { username ->
            tvName.text = username
        }

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Clear saved session
            sessionManager.clearSession()

            // Go back to login and clear back stack
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

            // --- Edit and delete buttons ---
        val btnEdit = findViewById<MaterialButton>(R.id.btnEdit)
        val btnDelete = findViewById<MaterialButton>(R.id.btnDelete)

        btnEdit.setOnClickListener {
            // Abrir la pantalla de actualización de clientes
            val intent = Intent(this, ActualizacionDeClientesActivity::class.java)
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            // Mostrar diálogo de confirmación para eliminar la cuenta
            showDeleteAccountDialog()
        }



        // --- Buttons: product list and compras ---
        val btnProductList = findViewById<Button>(R.id.btnProductList)
        btnProductList.setOnClickListener {
            val intent = Intent(this, ListaDeProductoActivity::class.java)
            startActivity(intent)
        }

        val btnBuy = findViewById<Button>(R.id.btnBuy)
        btnBuy.setOnClickListener {
            val intent = Intent(this, CompraActivity::class.java)
            startActivity(intent)
        }

        // --- Orders RecyclerView setup ---
        val rvOrders = findViewById<RecyclerView>(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)
        ordersAdapter = OrdersAdapter()
        rvOrders.adapter = ordersAdapter

        // --- Search field ---
        val etSearch = findViewById<TextInputEditText>(R.id.etSearch)
        etSearch.addTextChangedListener { text ->
            val query = text?.toString().orEmpty()
            applyOrderFilter(query)
        }

        // --- Room database for carts/orders ---
        val db = AppDatabase.getDatabase(applicationContext)
        val cartDao = db.cartDao()

        // Collect data from the database and update the RecyclerView
        lifecycleScope.launch {
            cartDao.getCartSummaries().collect { summaries ->
                allOrders = summaries
                // Apply current filter (if any)
                val currentQuery = etSearch.text?.toString().orEmpty()
                applyOrderFilter(currentQuery)
            }
        }
    }

    /**
     * Filtra la lista completa de órdenes por número, fecha, producto o monto.
     */
    private fun applyOrderFilter(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            ordersAdapter.submitList(allOrders)
            return
        }

        val lower = trimmed.lowercase()

        val filtered = allOrders.filter { order ->
            // Número de pedido (cartId)
            order.cartId.toString().contains(lower, ignoreCase = false) ||
                    // Fecha
                    order.creationDate.lowercase().contains(lower) ||
                    // Producto
                    order.productName.lowercase().contains(lower) ||
                    // Monto (totalPrice)
                    order.totalPrice.toString().contains(lower)
        }

        ordersAdapter.submitList(filtered)
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Tomar foto", "Elegir de la galería")

        AlertDialog.Builder(this)
            .setTitle("Cambiar foto de perfil")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    1 -> openImagePicker()
                }
            }
            .show()
    }

    // Opens gallery picker
    private fun openImagePicker() {
        pickImageLauncher.launch("image/*")
    }

    // Opens camera to take a picture and save it to MediaStore
    private fun openCamera() {
        val uri = createImageUri()   // Where the camera will save the photo
        if (uri != null) {
            tempPhotoUri = uri
            takePhotoLauncher.launch(uri)
        } else {
            Toast.makeText(this, "No se pudo crear el archivo de imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "perfil_${System.currentTimeMillis()}.jpg"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    // --- Delete account confirmation dialog ---
    private fun showDeleteAccountDialog() {
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()

        if (userId == -1) {
            Toast.makeText(this, "No se pudo obtener la información del usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val db = AppDatabase.getDatabase(applicationContext)
        val userDao = db.userDao()

        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_action, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)
        val btnNo = dialogView.findViewById<MaterialButton>(R.id.btnNo)
        val btnSi = dialogView.findViewById<MaterialButton>(R.id.btnSi)

        tvTitle.text = "¿Desea eliminar la cuenta?"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnClose.setOnClickListener { dialog.dismiss() }
        btnNo.setOnClickListener { dialog.dismiss() }

        btnSi.setOnClickListener {
            lifecycleScope.launch {
                val user = userDao.getUserById(userId)
                if (user != null) {
                    userDao.deleteUser(user)
                }

                // Cerrar sesión
                sessionManager.clearSession()

                // Mostrar confirmación
                Toast.makeText(
                    this@PerfilDeUsuarioActivity,
                    "Usuario eliminado",
                    Toast.LENGTH_SHORT
                ).show()

                // Ir a login y limpiar el back stack
                val intent = Intent(this@PerfilDeUsuarioActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                // Cerrar este Activity para que no se pueda volver atrás
                finish()

                // Cerrar el diálogo
                dialog.dismiss()
            }
        }

        dialog.show()
    }

}
