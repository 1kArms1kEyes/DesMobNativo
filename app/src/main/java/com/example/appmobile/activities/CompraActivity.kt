package com.example.appmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.database.AppDatabase
import com.example.appmobile.data.entities.Product
import com.example.appmobile.ui.viewmodels.adapters.CompraProductsAdapter
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class CompraActivity : AppCompatActivity() {

    private lateinit var adapter: CompraProductsAdapter

    // Lista ya agrupada por nombre (sin duplicados)
    private var allProducts: List<Product> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compra)

        // Back
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 1. RecyclerView
        val rvCompra = findViewById<RecyclerView>(R.id.rvProducts)
        rvCompra.layoutManager = LinearLayoutManager(this)

        // 2. Adapter: abre DetalleProductoActivity al hacer click
        adapter = CompraProductsAdapter { product ->
            val intent = Intent(this, DetalleProductoActivity::class.java)
            intent.putExtra(DetalleProductoActivity.EXTRA_PRODUCT_ID, product.productId)
            startActivity(intent)
        }
        rvCompra.adapter = adapter

        // 3. Switches de filtro
        val swHoodies = findViewById<SwitchMaterial>(R.id.swHoodies)
        val swSnapbacks = findViewById<SwitchMaterial>(R.id.swSnapbacks)
        val swCamisetas = findViewById<SwitchMaterial>(R.id.swCamisetas)
        val swTenis = findViewById<SwitchMaterial>(R.id.swTenis)

        // Listener compartido para todos los switches
        val filterChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            applyFilters(swHoodies, swSnapbacks, swCamisetas, swTenis)
        }

        swHoodies.setOnCheckedChangeListener(filterChangeListener)
        swSnapbacks.setOnCheckedChangeListener(filterChangeListener)
        swCamisetas.setOnCheckedChangeListener(filterChangeListener)
        swTenis.setOnCheckedChangeListener(filterChangeListener)

        // 4. Cargar productos desde la BD
        val db = AppDatabase.getDatabase(applicationContext)
        val productDao = db.productDao()

        lifecycleScope.launch {
            productDao.getAllProducts().collect { products ->
                // Solo activos y sin duplicar nombres
                allProducts = products
                    .filter { it.isActive }
                    .groupBy { it.name }
                    .map { (_, variants) ->
                        // Un representante por nombre
                        variants.first()
                    }

                // Cada vez que cambian los datos, volvemos a aplicar filtros
                applyFilters(swHoodies, swSnapbacks, swCamisetas, swTenis)
            }
        }

        // 5. Botón de carrito
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        btnCart.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Aplica los filtros según el estado de los 4 switches.
     * Si ningún switch está encendido, no se muestra ningún producto.
     */
    private fun applyFilters(
        swHoodies: SwitchMaterial,
        swSnapbacks: SwitchMaterial,
        swCamisetas: SwitchMaterial,
        swTenis: SwitchMaterial
    ) {
        if (allProducts.isEmpty()) {
            adapter.submitList(emptyList())
            return
        }

        // IDs de categoría según CreacionDeProductosActivity:
        // 1 = Hoodies, 2 = Camisetas, 3 = Snapbacks, 4 = Tenis
        val enabledCategoryIds = mutableSetOf<Int>()
        if (swHoodies.isChecked) enabledCategoryIds.add(1)
        if (swCamisetas.isChecked) enabledCategoryIds.add(2)
        if (swSnapbacks.isChecked) enabledCategoryIds.add(3)
        if (swTenis.isChecked) enabledCategoryIds.add(4)

        val filtered = if (enabledCategoryIds.isEmpty()) {
            // Si todos apagados, lista vacía
            emptyList()
        } else {
            allProducts.filter { it.categoryId in enabledCategoryIds }
        }

        adapter.submitList(filtered)
    }
}
