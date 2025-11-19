package com.example.appmobile.activities

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.ui.viewmodels.adapters.OrdersAdapter
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class PerfilDeUsuarioActivity : AppCompatActivity() {

    // Adapter for the RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter

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
                // Here you could persist the URI in SharedPreferences / DB if desired
            }
        }

    // Launcher to take a picture with the camera and save it into tempPhotoUri
    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success && tempPhotoUri != null) {
                // Set the captured image as avatar
                imgAvatar.setImageURI(tempPhotoUri)
                Toast.makeText(this, "Foto guardada en la galería", Toast.LENGTH_SHORT).show()
                // Again, you could persist tempPhotoUri.toString() if you want to reload it later.
            } else {
                Toast.makeText(this, "No se tomó la foto", Toast.LENGTH_SHORT).show()
            }
        }

    // Request CAMERA permission
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

        // When the camera icon is clicked, open the image picker
        ivCamera.setOnClickListener {
            showImageSourceDialog()
        }

        val btnProductList = findViewById<Button>(R.id.btnProductList)
        btnProductList.setOnClickListener {
            val intent = Intent(this, ListaDeProductoActivity::class.java)
            startActivity(intent)
        }

        // --- "Compras" button opens CompraActivity ---
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

        // --- Room database for carts/orders ---
        val db = AppDatabase.getDatabase(applicationContext)
        val cartDao = db.cartDao()

        // --- Collect data from the database and update the RecyclerView ---
        lifecycleScope.launch {
            cartDao.getCartSummaries().collect { summaries ->
                ordersAdapter.submitList(summaries)
            }
        }
    }

    // Shows a dialog to choose between camera or gallery
    private fun showImageSourceDialog() {
        val options = arrayOf("Tomar foto", "Elegir de la galería")
        AlertDialog.Builder(this)
            .setTitle("Seleccionar foto de perfil")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Camera
                        requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                    1 -> { // Gallery
                        openImagePicker()
                    }
                }
            }
            .show()
    }

    // Opens gallery picker
    private fun openImagePicker() {
        // "image/*" tells Android we only want images
        pickImageLauncher.launch("image/*")
    }
}
