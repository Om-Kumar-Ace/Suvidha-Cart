package com.example.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cart.R
import com.example.cart.model.Transaction

class HistoryAdapter(
    private val context: Context,
    private val transactions: List<Transaction>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.transaction_date)
        val totalAmountTextView: TextView = view.findViewById(R.id.transaction_total)
        val paymentMethodTextView: TextView = view.findViewById(R.id.transaction_payment_method)
        val itemsContainer: LinearLayout = view.findViewById(R.id.items_container)
        val viewDetailsButton: TextView = view.findViewById(R.id.view_details_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.history_list_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.dateTextView.text = transaction.timestamp
        holder.totalAmountTextView.text = "₹${transaction.totalAmount}"
        holder.paymentMethodTextView.text = "Paid via ${transaction.paymentMethod}"

        // Show items on button click
        holder.viewDetailsButton.setOnClickListener {
            if (holder.itemsContainer.visibility == View.GONE) {
                holder.itemsContainer.visibility = View.VISIBLE
                holder.viewDetailsButton.text = "Hide Details"
                populateItems(holder.itemsContainer, transaction.items)
            } else {
                holder.itemsContainer.visibility = View.GONE
                holder.viewDetailsButton.text = "View Details"
            }
        }
    }

    override fun getItemCount(): Int = transactions.size

    // Populate purchased items inside the transaction
    private fun populateItems(container: LinearLayout, items: List<Map<String, Any>>) {
        container.removeAllViews()
        for (item in items) {
            val itemView = LayoutInflater.from(context).inflate(R.layout.bill_item, container, false)
            itemView.findViewById<TextView>(R.id.bill_item_title).text = item["title"].toString()
            itemView.findViewById<TextView>(R.id.bill_item_price).text = "₹${item["price"]}"
            itemView.findViewById<TextView>(R.id.bill_item_quantity).text = "Qty: ${item["quantity"]}"
            container.addView(itemView)
        }
    }
}
