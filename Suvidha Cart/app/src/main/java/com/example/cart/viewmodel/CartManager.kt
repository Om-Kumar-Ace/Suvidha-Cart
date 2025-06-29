package com.example.cart.viewmodel

import com.example.cart.model.CartItem
import com.google.firebase.firestore.FirebaseFirestore

class CartManager {
    private val db = FirebaseFirestore.getInstance()

    fun addToCart(userId: String, item: CartItem) {
        db.collection("Users").document(userId).collection("Cart")
            .document(item.productId).set(item)
            .addOnSuccessListener { println("Item added to cart") }
            .addOnFailureListener { e -> println("Error: $e") }
    }

    fun removeFromCart(userId: String, productId: String) {
        db.collection("Users").document(userId).collection("Cart")
            .document(productId).delete()
            .addOnSuccessListener { println("Item removed from cart") }
            .addOnFailureListener { e -> println("Error: $e") }
    }
}
