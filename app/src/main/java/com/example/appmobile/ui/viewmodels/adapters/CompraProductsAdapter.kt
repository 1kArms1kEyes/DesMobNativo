package com.example.appmobile.ui.viewmodels.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.entities.Product
import com.google.android.material.imageview.ShapeableImageView

class CompraProductsAdapter :
    RecyclerView.Adapter<CompraProductsAdapter.ProductViewHolder>() {

    private var items: List<Product> = emptyList()

    fun submitList(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ShapeableImageView = itemView.findViewById(R.id.imgProduct)
        val tvName: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]

        holder.tvName.text = item.name
        holder.tvPrice.text = "$${item.price}"

        // Default image in XML will be used
        // If you want to load from URL later, use Glide/Picasso here.
    }

    override fun getItemCount(): Int = items.size
}