package com.example.cart.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cart.R
import com.example.cart.adapter.CartAdapter
import com.example.cart.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.progressindicator.CircularProgressIndicator

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var loaderLayout: CircularProgressIndicator
    private lateinit var emptyTextView: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.cart_products_recycler_view)
        loaderLayout = view.findViewById(R.id.loader_layout)
        emptyTextView = view.findViewById(R.id.cart_empty_text_view)

        setupRecyclerView()
        fetchCartItems()
    }

    private fun setupRecyclerView() {


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = cartAdapter
    }

    private fun fetchCartItems() {
        val userId = auth.currentUser?.uid ?: return
        val cartCollection = firestore.collection("users").document(userId).collection("cart")

        loaderLayout.visibility = View.VISIBLE

        cartCollection.get()
            .addOnSuccessListener { documents ->
                cartItems.clear()
                for (document in documents) {
                    val item = document.toObject(CartItem::class.java)
                    cartItems.add(item)
                }
                cartAdapter.updateCart(cartItems)
                toggleEmptyState()
                loaderLayout.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to load cart: ${exception.message}", Toast.LENGTH_SHORT).show()
                loaderLayout.visibility = View.GONE
            }
    }

    private fun toggleEmptyState() {
        emptyTextView.visibility = if (cartItems.isEmpty()) View.VISIBLE else View.GONE
    }
}
