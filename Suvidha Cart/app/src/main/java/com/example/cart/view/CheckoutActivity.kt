package com.example.cart.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cart.R
import com.example.cart.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.*
class CheckoutActivity : AppCompatActivity() {
    private var totalAmountTextView: TextView? = null
    private var paymentOptionsGroup: RadioGroup? = null
    private var confirmPaymentButton: Button? = null
    private var totalAmount = 0.0
    private var cartItems: List<CartItem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        totalAmountTextView = findViewById(R.id.checkout_total_amount)
        paymentOptionsGroup = findViewById(R.id.payment_options_group)
        confirmPaymentButton = findViewById(R.id.confirm_payment_button)
        cartItems = intent.getParcelableArrayListExtra("cart_items") ?: listOf()
        totalAmount = intent.getDoubleExtra("total_amount", 0.0)

        // ✅ FIXED: Use safe call (?) to prevent NullPointerException
        totalAmountTextView?.text = String.format("Total: ₹ %.2f", totalAmount)

        // ✅ FIXED: Use safe call (?.) instead of !! to prevent crashes
        confirmPaymentButton?.setOnClickListener { processPayment() }

    }

    private fun processPayment() {
        val selectedId = paymentOptionsGroup?.checkedRadioButtonId ?: -1
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPayment = findViewById<RadioButton>(selectedId)
        val paymentMethod = selectedPayment.text.toString()

        // Show success message
        Toast.makeText(this, "Payment successful using $paymentMethod", Toast.LENGTH_LONG).show()

        // Store transaction details and clear cart
        saveTransaction(paymentMethod)
    }
    private fun clearCart(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val cartCollection = firestore.collection("users").document(userId).collection("cart")

        cartCollection.get().addOnSuccessListener { documents ->
            for (document in documents) {
                cartCollection.document(document.id).delete()
            }
            Toast.makeText(this, "Cart cleared!", Toast.LENGTH_SHORT).show()

            // Navigate to History Page
            goToHistoryPage()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to clear cart!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveTransaction(paymentMethod: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        val timestamp = System.currentTimeMillis()
        val formattedDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))

        val transaction = hashMapOf(
            "userId" to userId,
            "totalAmount" to totalAmount,
            "paymentMethod" to paymentMethod,
            "timestamp" to formattedDate,
            "items" to cartItems.map { item ->  // cartItems is now properly initialized ✅
                mapOf(
                    "title" to item.title,
                    "price" to item.price,
                    "quantity" to item.quantity
                )
            }
        )

        firestore.collection("users").document(userId)
            .collection("history").add(transaction)
            .addOnSuccessListener {
                clearCart(userId) // Clear cart after transaction is stored
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save transaction!", Toast.LENGTH_SHORT).show()
            }
    }


    private fun goToHistoryPage() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()  // Close checkout activity
    }
}
