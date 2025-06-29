package com.example.cart.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cart.R
import com.example.cart.adapter.CartAdapter
import com.example.cart.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CartActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var loaderLayout: FrameLayout
    private lateinit var emptyTextView: TextView
    private lateinit var totalAmountTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var budgetEditText: EditText
    private lateinit var saveBudgetButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    private var cartItems = mutableListOf<CartItem>()
    private var totalAmount = 0.0
    private var budgetLimit = 1000.0
    private var cartListener: ListenerRegistration? = null // Firestore real-time listener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE)
        budgetLimit = sharedPreferences.getFloat("budget_limit", 1000.0f).toDouble()

        initViews()
        setupRecyclerView()
        listenToCartUpdates(userId)

        saveBudgetButton.setOnClickListener { updateBudget() }
        checkoutButton.setOnClickListener { goToCheckoutPage() }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.cart_products_recycler_view)
        loaderLayout = findViewById(R.id.loader_layout)
        emptyTextView = findViewById(R.id.cart_empty_text_view)
        totalAmountTextView = findViewById(R.id.cart_total_amount)
        checkoutButton = findViewById(R.id.cart_check_out_btn)
        budgetEditText = findViewById(R.id.budget_edit_text)
        saveBudgetButton = findViewById(R.id.save_budget_button)

        budgetEditText.setText(budgetLimit.toString())
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            context = this,
            cartItems = cartItems,
            onItemRemoved = { item -> removeCartItem(item) },
            onQuantityChanged = { item, newQuantity -> updateCartItemQuantity(item, newQuantity) },
            userId = auth.currentUser?.uid ?: ""
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)  // 🔥 Fix UI refresh issues
        recyclerView.adapter = cartAdapter
    }


    private fun listenToCartUpdates(userId: String) {
        val cartCollection = firestore.collection("users").document(userId).collection("cart")

        cartListener?.remove()
        cartListener = cartCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("CartActivity", "Failed to load cart: ${error.message}")
                loaderLayout.visibility = View.GONE
                return@addSnapshotListener
            }

            val newCartItems = mutableListOf<CartItem>()
            totalAmount = 0.0

            snapshots?.forEach { document ->
                val item = document.toObject(CartItem::class.java)

                // 🔥 Debug Firestore data
                Log.d("CartActivity", "Fetched item: Title=${item.title}, Price=${item.price}, Quantity=${item.quantity}, Image=${item.imageUrl}")

                if (item.title.isNotEmpty()) {
                    newCartItems.add(item)
                    totalAmount += item.price * item.quantity
                } else {
                    Log.e("CartActivity", "Skipping item due to missing title: ${document.id}")
                }
            }

            if (newCartItems.isEmpty()) {
                Log.w("CartActivity", "No valid cart items found!")
            }

            cartItems.clear()
            cartItems.addAll(newCartItems)
            cartAdapter.updateCart(cartItems)

            updateTotalAmount()
            toggleEmptyState()
            loaderLayout.visibility = View.GONE
        }
    }


    private fun updateTotalAmount() {
        totalAmountTextView.text = "Total: ₹%.2f".format(totalAmount)
    }

    private fun toggleEmptyState() {
        emptyTextView.visibility = if (cartItems.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun goToCheckoutPage() {
        val intent = Intent(this, CheckoutActivity::class.java)
        intent.putParcelableArrayListExtra("cart_items", ArrayList(cartItems)) // Pass cart items
        intent.putExtra("total_amount", totalAmount) // Pass total amount
        startActivity(intent)
    }


    private fun updateBudget() {
        val newBudget = budgetEditText.text.toString().toDoubleOrNull()
        if (newBudget != null && newBudget > 0) {
            budgetLimit = newBudget
            sharedPreferences.edit().putFloat("budget_limit", budgetLimit.toFloat()).apply()
            Toast.makeText(this, "Budget updated to ₹$budgetLimit", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Enter a valid budget amount", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeCartItem(item: CartItem) {
        firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("cart")
            .document(item.productId)
            .delete()
            .addOnSuccessListener {
                Log.d("CartActivity", "Item removed: ${item.productId}")
            }
            .addOnFailureListener { e ->
                Log.e("CartActivity", "Error removing item: ${e.message}")
            }
    }

    private fun updateCartItemQuantity(item: CartItem, newQuantity: Int) {
        val cartDocRef = firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("cart")
            .document(item.productId)

        cartDocRef.update("quantity", newQuantity)
            .addOnSuccessListener {
                Log.d("CartActivity", "Quantity updated for ${item.productId} to $newQuantity")
            }
            .addOnFailureListener { e ->
                Log.e("CartActivity", "Error updating quantity: ${e.message}")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        cartListener?.remove()
    }
}
