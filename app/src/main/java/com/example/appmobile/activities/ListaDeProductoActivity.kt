package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.example.appmobile.R
import kotlinx.coroutines.launch

import com.example.appmobile.activities.CreacionDeProductosActivity
import com.example.appmobile.activities.EditarProductosActivity
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.dao.ProductDao
import com.example.appmobile.data.entities.Product
import com.example.appmobile.ui.viewmodels.adapters.AdminProductsAdapter

class ListaDeProductoActivity : AppCompatActivity() {
    private lateinit var productsAdapter: AdminProductsAdapter
    private lateinit var productDao: ProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_de_producto)

        val db = AppDatabase.getDatabase(this)
        productDao = db.productDao()

        val btnAdd = findViewById<ImageView>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(this, CreacionDeProductosActivity::class.java)
            startActivity(intent)
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val rvAdminProducts = findViewById<RecyclerView>(R.id.rvAdminProducts)
        rvAdminProducts.layoutManager = LinearLayoutManager(this)

        productsAdapter = AdminProductsAdapter(
            onEditClick = { product ->
                editProduct(product)
            },
            onDeleteClick = { product ->
                showDeleteProductDialog(product)
            }
        )

        rvAdminProducts.adapter = productsAdapter

        lifecycleScope.launch {
            productDao.getAllProducts().collect { products ->
                productsAdapter.submitList(products)
            }
        }
    }

    private fun editProduct(product: Product) {
        val intent = Intent(this, EditarProductosActivity::class.java)
        intent.putExtra("product_id", product.productId)
        startActivity(intent)
    }

    private fun showDeleteProductDialog(product: Product) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_action, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "Eliminar producto"

        val btnNo = dialogView.findViewById<MaterialButton>(R.id.btnNo)
        val btnSi = dialogView.findViewById<MaterialButton>(R.id.btnSi)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        btnSi.setOnClickListener {
            deleteProduct(product)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun deleteProduct(product: Product) {
        lifecycleScope.launch {
            productDao.deleteProduct(product)

            Toast.makeText(
                this@ListaDeProductoActivity,
                "Producto eliminado correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
