package com.example.appmobile.ui.viewmodels.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.entities.Product
import com.google.android.material.imageview.ShapeableImageView

// Adapter that shows a list of Product items using item_product_admin.xml
class AdminProductsAdapter : RecyclerView.Adapter<AdminProductsAdapter.ProductViewHolder>() {

    // List of products that will be shown in the RecyclerView
    private var items: List<Product> = emptyList()

    // Call this from the Activity when you have a new list of products
    fun submitList(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }

    // ViewHolder = one card view (one row in the list)
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ShapeableImageView = itemView.findViewById(R.id.imgProduct)
        val tvCode: TextView = itemView.findViewById(R.id.tvCode)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    // Called when RecyclerView needs to create a new card
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_admin, parent, false)
        return ProductViewHolder(view)
    }

    // Called for each card to put the product data into the views
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]

        // Show something like "Producto No. 1", using the product_id from the database
        // (Product has a productId property mapped to the "product_id" column)
        holder.tvCode.text = "Producto No. ${item.productId}"

        // Show the product name
        holder.tvName.text = item.name

        // For now, we keep the sample image defined in XML.
        // Later, we can load item.imageUrl using an image loading library like Glide or Picasso.

        // Optional: you can set up click listeners for edit/delete here
        holder.btnEdit.setOnClickListener {
            // TODO: open edit screen for this product
        }

        holder.btnDelete.setOnClickListener {
            // TODO: show confirmation and delete this product
        }
    }

    // How many items in the list?
    override fun getItemCount(): Int = items.size
}