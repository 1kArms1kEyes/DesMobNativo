package com.example.appmobile.ui.viewmodels.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.entities.CartSummary

// This adapter shows a list of CartSummary items using the item_order.xml layout
class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    // This is the list of orders that the RecyclerView will show
    private var items: List<CartSummary> = emptyList()

    // Call this from your Activity when you have new data
    fun submitList(newItems: List<CartSummary>) {
        items = newItems
        notifyDataSetChanged()  // Tell RecyclerView: "Hey, the data changed, redraw!"
    }

    // 1 row (card) = 1 ViewHolder
    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvOrderNumber: TextView = itemView.findViewById(R.id.tvOrderNumber)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        // imgProduct exists too, but for now we leave the default image set in XML
    }

    // Called when RecyclerView needs to create a new card (ViewHolder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        // Inflate (create) the view from your item_order.xml file
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    // Called for each row to put the data into the card
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]  // Get the CartSummary at this position

        // Set the date text
        holder.tvDate.text = item.creationDate

        // Set the order number (Pedido #X) -> here we just show the cartId
        holder.tvOrderNumber.text = item.cartId.toString()

        // Set the product name
        holder.tvProductName.text = item.productName

        // Format the price, e.g. $1,234.50
        val formattedPrice = String.format("$%,.2f", item.totalPrice)
        holder.tvPrice.text = "Total: $formattedPrice"
    }

    // How many items in the list?
    override fun getItemCount(): Int = items.size
}