package com.example.cart.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cart.R
import com.example.cart.adapter.HistoryAdapter
import com.example.cart.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private val transactions = mutableListOf<Transaction>()
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.history_recycler_view)
        setupRecyclerView()
        fetchTransactions()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(this, transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter
    }

    private fun fetchTransactions() {
        firestore.collection("users").document(userId)
            .collection("history")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                transactions.clear()
                for (document in documents) {
                    val transaction = document.toObject(Transaction::class.java)
                    transactions.add(transaction)
                }
                historyAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load history!", Toast.LENGTH_SHORT).show()
            }
    }
}
