package com.example.cart.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cart.R
import com.example.cart.model.CartItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CartAdapter(
    private val context: Context,
    private var cartItems: MutableList<CartItem>,
    private val onItemRemoved: (CartItem) -> Unit,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val userId: String
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private var cartListener: ListenerRegistration? = null

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.product_image_view)
        val productTitle: TextView = view.findViewById(R.id.cart_product_title_tv)
        val productPrice: TextView = view.findViewById(R.id.cart_product_price_tv)
        val productQuantity: TextView = view.findViewById(R.id.cart_product_quantity_text_View)
        val btnDelete: ImageButton = view.findViewById(R.id.cart_product_delete_btn)
        val btnMinus: ImageButton = view.findViewById(R.id.cart_product_minus_btn)
        val btnPlus: ImageButton = view.findViewById(R.id.cart_product_plus_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_list_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        Log.d("CartAdapter", "Binding item: ${item.title}, Quantity: ${item.quantity}")

        // Bind data to views
        holder.productTitle.text = item.title
        holder.productPrice.text = "\u20B9${String.format("%.2f", item.price)}"
        holder.productQuantity.text = item.quantity.toString()

        // Load image using Glide
        Glide.with(context)
            .load(item.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_cart_image)
            .into(holder.productImage)

        // Handle remove button
        holder.btnDelete.setOnClickListener {
            removeItem(item)
        }

        // Handle quantity decrement
        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                updateQuantity(item, item.quantity - 1)
            }
        }

        // Handle quantity increment
        holder.btnPlus.setOnClickListener {
            updateQuantity(item, item.quantity + 1)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    /**
     * Fetch cart items from Firestore using real-time listener
     */
    fun fetchCartItems() {
        val cartCollection = firestore.collection("users").document(userId).collection("cart")

        cartListener?.remove() // 🔥 Remove old listener to prevent duplicates
        cartListener = cartCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("CartAdapter", "Failed to load cart items: ${error.message}")
                return@addSnapshotListener
            }

            val newCartItems = mutableListOf<CartItem>()

            snapshots?.forEach { document ->
                val item = document.toObject(CartItem::class.java)

                // 🔥 Debug: Log Firestore data before adding
                Log.d("CartAdapter", "Fetched item: ${document.id} | Title=${item.title}, Price=${item.price}, Quantity=${item.quantity}, Image=${item.imageUrl}")

                if (item.title.isNotEmpty()) {  // 🔥 Only add valid items
                    newCartItems.add(item)
                } else {
                    Log.e("CartAdapter", "Skipping item due to missing title: ${document.id}")
                }
            }

            if (newCartItems.isEmpty()) {
                Log.w("CartAdapter", "Firestore returned no valid items!")
            }

            cartItems.clear()
            cartItems.addAll(newCartItems)

            Log.d("CartAdapter", "Final item count after Firestore fetch: ${cartItems.size}")

            if (cartItems.isEmpty()) {
                Log.e("CartAdapter", "cartItems is still empty after adding new items! 🔥 Check Firestore fields")
            } else {
                notifyDataSetChanged() // 🔥 Ensure UI updates
            }
        }
    }



    /**
     * Updates the quantity of a cart item in Firestore
     */
    private fun updateQuantity(item: CartItem, newQuantity: Int) {
        val cartDocRef = firestore.collection("users")
            .document(userId)
            .collection("cart")
            .document(item.productId)

        cartDocRef.update("quantity", newQuantity)
            .addOnSuccessListener {
                Log.d("CartAdapter", "Quantity updated for ${item.productId} to $newQuantity")
                onQuantityChanged(item, newQuantity)
            }
            .addOnFailureListener { e ->
                Log.e("CartAdapter", "Error updating quantity: ${e.message}")
            }
    }

    /**
     * Removes an item from Firestore and updates RecyclerView
     */
    private fun removeItem(item: CartItem) {
        val cartDocRef = firestore.collection("users")
            .document(userId)
            .collection("cart")
            .document(item.productId)

        cartDocRef.delete()
            .addOnSuccessListener {
                Log.d("CartAdapter", "Item removed: ${item.productId}")
                onItemRemoved(item)
            }
            .addOnFailureListener { e ->
                Log.e("CartAdapter", "Error removing item: ${e.message}")
            }
    }
    fun updateCart(newItems: List<CartItem>) {
        Log.d("CartAdapter", "Updating cart with ${newItems.size} items")

        if (newItems.isEmpty()) {
            Log.w("CartAdapter", "No new items received. Keeping existing cart items.")
            return
        }



        Log.d("CartAdapter", "Final item count after update: ${cartItems.size}")

        if (cartItems.isEmpty()) {
            Log.e("CartAdapter", "cartItems is still empty after adding new items! 🔥 Check Firestore fields")
        } else {
            notifyDataSetChanged()  // 🔥 Force RecyclerView to refresh
        }

        cartItems.forEach {
            Log.d("CartAdapter", "Item in RecyclerView: ${it.title}, Quantity: ${it.quantity}, Price: ${it.price}")
        }
    }




    /**
     * Clean up Firestore listener to prevent memory leaks
     */
    fun removeListener() {
        cartListener?.remove()
        cartListener = null
    }
}
