package com.example.appmobile.ui.viewmodels.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appmobile.R
import com.example.appmobile.data.entities.Product
import com.google.android.material.imageview.ShapeableImageView

class AdminProductsAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductsAdapter.ProductViewHolder>() {

    private var items: List<Product> = emptyList()

    fun submitList(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ShapeableImageView = itemView.findViewById(R.id.imgProduct)
        val tvCode: TextView = itemView.findViewById(R.id.tvCode)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_admin, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.tvCode.text = "Producto No. ${item.productId}"
        holder.tvName.text = item.name

        // Imagen: URL → Glide, nombre de drawable → recurso local, sino placeholder
        when {
            item.imageUrl.isNotBlank() &&
                    (item.imageUrl.startsWith("http://") || item.imageUrl.startsWith("https://")) -> {
                Glide.with(context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.sample_product_large)
                    .error(R.drawable.sample_product_large)
                    .into(holder.imgProduct)
            }
            item.imageUrl.isNotBlank() -> {
                val resId = context.resources.getIdentifier(
                    item.imageUrl,
                    "drawable",
                    context.packageName
                )
                if (resId != 0) {
                    holder.imgProduct.setImageResource(resId)
                } else {
                    holder.imgProduct.setImageResource(R.drawable.sample_product_large)
                }
            }
            else -> {
                holder.imgProduct.setImageResource(R.drawable.sample_product_large)
            }
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(item)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
