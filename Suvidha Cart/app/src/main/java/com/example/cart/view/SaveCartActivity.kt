package com.example.cart.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cart.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SaveCartActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var productIdEditText: EditText
    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productQuantityEditText: EditText
    private lateinit var productImageUrlEditText: EditText
    private lateinit var saveButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_cart)

        // Initialize Firebase Firestore & Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Bind UI elements
        productIdEditText = findViewById(R.id.product_id_edit_text)
        productNameEditText = findViewById(R.id.product_name_edit_text)
        productPriceEditText = findViewById(R.id.product_price_edit_text)
        productQuantityEditText = findViewById(R.id.product_quantity_edit_text)
        productImageUrlEditText = findViewById(R.id.product_image_url_edit_text)
        saveButton = findViewById(R.id.save_cart_button)

        // Set onClickListener for saving cart item
        saveButton.setOnClickListener {
            saveCartToFirestore()
        }
    }

    private fun saveCartToFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val cartCollection = firestore.collection("users").document(userId).collection("cart")

        val productId = productIdEditText.text.toString().trim()
        val productName = productNameEditText.text.toString().trim()
        val productPrice = productPriceEditText.text.toString().toDoubleOrNull()
        val productQuantity = productQuantityEditText.text.toString().toIntOrNull()
        val productImageUrl = productImageUrlEditText.text.toString().trim()

        // Validate inputs
        if (productId.isEmpty() || productName.isEmpty() || productPrice == null || productQuantity == null || productQuantity <= 0 || productImageUrl.isEmpty()) {
            Toast.makeText(this, "Please enter all product details correctly", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val cartItem = hashMapOf(
            "productId" to productId,
            "title" to productName,
            "price" to productPrice,
            "quantity" to productQuantity,
            "imageUrl" to productImageUrl
        )

        cartCollection.document(productId).set(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Cart item saved successfully!", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        productIdEditText.text.clear()
        productNameEditText.text.clear()
        productPriceEditText.text.clear()
        productQuantityEditText.text.clear()
        productImageUrlEditText.text.clear()
    }
}
