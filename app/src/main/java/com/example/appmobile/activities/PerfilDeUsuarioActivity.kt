package com.example.appmobile.activities

import android.net.Uri
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.ui.viewmodels.adapters.OrdersAdapter

class PerfilDeUsuarioActivity : AppCompatActivity() {

    // Adapter for the RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter

    // Views for the avatar and camera icon
    private lateinit var imgAvatar: ShapeableImageView
    private lateinit var ivCamera: ImageView

    // Launcher to open the image picker and get the selected image URI
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // Set the selected image into the avatar ImageView
                imgAvatar.setImageURI(uri)
                // If later you want to save this URI to DB or preferences, you can do it here.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_de_usuario)

        // --- Find views for avatar and camera ---
        imgAvatar = findViewById(R.id.imgAvatar)
        ivCamera = findViewById(R.id.ivCamera)

        // When the camera icon is clicked, open the image picker
        ivCamera.setOnClickListener {
            openImagePicker()
        }

        val btnProductList = findViewById<Button>(R.id.btnProductList)
        btnProductList.setOnClickListener {
            val intent = Intent(this, ListaDeProductoActivity::class.java)
            startActivity(intent)
        }

        val btnBuyList = findViewById<Button>(R.id.btnBuy)
        btnBuyList.setOnClickListener {
            val intent = Intent(this, ComprasActivity::class.java)
            startActivity(intent)
        }

        // --- 1. Setup RecyclerView ---
        val rvOrders = findViewById<RecyclerView>(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)
        ordersAdapter = OrdersAdapter()
        rvOrders.adapter = ordersAdapter

        // --- 2. Get a reference to your Room database ---
        val db = AppDatabase.getDatabase(applicationContext)
        val cartDao = db.cartDao()

        // --- 3. Collect data from the database and update the RecyclerView ---
        lifecycleScope.launch {
            cartDao.getCartSummaries().collect { summaries ->
                ordersAdapter.submitList(summaries)
            }
        }
    }

    private fun openImagePicker() {
        // "image/*" tells Android we only want images
        pickImageLauncher.launch("image/*")
    }
}
