package com.example.appmobile.ui.viewmodels.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.entities.CartSummary

/**
 * Adapter to show a list of CartSummary items using item_order.xml.
 * It exposes submitList() so that the Activity can filter/search easily.
 */
class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    // Current list of items being displayed
    private var items: List<CartSummary> = emptyList()

    /**
     * Replaces the current list and refreshes the RecyclerView.
     */
    fun submitList(newItems: List<CartSummary>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]

        // Set the date
        holder.tvDate.text = item.creationDate

        // Set the order number (Pedido #X) -> we just show the cartId
        holder.tvOrderNumber.text = item.cartId.toString()

        // Set the product name
        holder.tvProductName.text = item.productName

        // Format the price, e.g. $1,234.50
        val formattedPrice = String.format("$%,.2f", item.totalPrice)
        holder.tvPrice.text = "Total: $formattedPrice"
    }

    override fun getItemCount(): Int = items.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvOrderNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
    }
}
