package com.example.appmobile.ui.viewmodels.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.entities.CartItemDetail
import com.google.android.material.imageview.ShapeableImageView

class CartItemsAdapter : RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder>() {

    private var items: List<CartItemDetail> = emptyList()

    fun submitList(newItems: List<CartItemDetail>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Match IDs from activity_item_carrito.xml

        // Product title/name
        val tvName: TextView? = itemView.findViewById(R.id.tvTitle)

        // Size
        val tvSize: TextView? = itemView.findViewById(R.id.tvSize)

        // Quantity
        val tvQuantity: TextView? = itemView.findViewById(R.id.tvQty)

        // Unit price
        val tvUnitPrice: TextView? = itemView.findViewById(R.id.tvUnitPrice)

        // Total price (price x quantity)
        val tvLineTotal: TextView? = itemView.findViewById(R.id.tvPrice)

        // Product image
        val imgProduct: ShapeableImageView? = itemView.findViewById(R.id.imgProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_carrito, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = items[position]

        // Product name
        holder.tvName?.text = item.productName

        // Size
        holder.tvSize?.text = item.size

        // Quantity
        holder.tvQuantity?.text = item.quantity.toString()

        // Unit price
        val unitFormatted = String.format("$%,.2f", item.unitPrice)
        holder.tvUnitPrice?.text = unitFormatted

        // Total price = lineTotal (already price * quantity from DAO)
        val totalFormatted = String.format("$%,.2f", item.lineTotal)
        holder.tvLineTotal?.text = totalFormatted


    }

    override fun getItemCount(): Int = items.size
}
