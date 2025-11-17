package com.example.appmobile.ui.viewmodels.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmobile.R
import com.example.appmobile.data.entities.CartItemDetail
import com.google.android.material.imageview.ShapeableImageView

class CartItemsAdapter(
    private val listener: OnCartItemInteractionListener? = null
) : RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder>() {

    interface OnCartItemInteractionListener {
        fun onIncreaseQuantity(item: CartItemDetail)
        fun onDecreaseQuantity(item: CartItemDetail)
        fun onRemoveItem(item: CartItemDetail)
    }

    private var items: List<CartItemDetail> = emptyList()

    fun submitList(newItems: List<CartItemDetail>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Match IDs from activity_item_carrito.xml
        val tvName: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSize: TextView = itemView.findViewById(R.id.tvSize)
        val tvColor: TextView = itemView.findViewById(R.id.tvColor)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQty)
        val tvUnitPrice: TextView = itemView.findViewById(R.id.tvUnitPrice)
        val tvLineTotal: TextView = itemView.findViewById(R.id.tvPrice)

        val imgProduct: ShapeableImageView = itemView.findViewById(R.id.imgProduct)

        val btnDecrease: View = itemView.findViewById(R.id.btnDec)
        val btnIncrease: View = itemView.findViewById(R.id.btnInc)
        val btnRemove: View = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_carrito, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        // Nombre
        holder.tvName.text = item.productName

        // Talla
        holder.tvSize.text = item.size

        // Color (THIS is what fixes the "Azul y blanco" always issue)
        holder.tvColor.text = item.color

        // Cantidad
        holder.tvQuantity.text = item.quantity.toString()

        // Precio unitario
        holder.tvUnitPrice.text = String.format("$%,.2f", item.unitPrice)

        // Total de la línea
        holder.tvLineTotal.text = String.format("$%,.2f", item.lineTotal)

        // Imagen del producto (por nombre de recurso si no es URL http)
        if (item.imageUrl.isNotBlank() && !item.imageUrl.startsWith("http")) {
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
        } else {
            holder.imgProduct.setImageResource(R.drawable.sample_product_large)
        }

        // Botones de interacción
        holder.btnIncrease.setOnClickListener {
            listener?.onIncreaseQuantity(item)
        }

        holder.btnDecrease.setOnClickListener {
            listener?.onDecreaseQuantity(item)
        }

        holder.btnRemove.setOnClickListener {
            listener?.onRemoveItem(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
