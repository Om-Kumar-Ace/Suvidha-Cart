package com.example.cart.viewmodel

import com.example.cart.model.CartItem
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutManager {
    private val db = FirebaseFirestore.getInstance()

    fun processOrder(userId: String, cartItems: List<CartItem>) {
        val orderRef = db.collection("Users").document(userId).collection("Orders").document()
        orderRef.set(hashMapOf("items" to cartItems, "status" to "pending"))
            .addOnSuccessListener { println("Order placed successfully") }
            .addOnFailureListener { e -> println("Error: $e") }
    }
}
