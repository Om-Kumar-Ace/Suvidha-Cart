package com.example.cart.view

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cart.R
import com.example.cart.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Aktivitas untuk Sign In
 */
class SignIn : AppCompatActivity() {
    // Deklarasi variabel UI
    private lateinit var emailEt: EditText
    private lateinit var passEt: EditText
    private lateinit var signInButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Inisialisasi variabel UI
        emailEt = findViewById(R.id.textInputEditText)
        passEt = findViewById(R.id.editTextPassword)
        signInButton = findViewById(R.id.button6)

        // Inisialisasi Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Set listener untuk tombol Sign In
        signInButton.setOnClickListener {
            val email = emailEt.text.toString()
            val pass = passEt.text.toString()

            // Cek apakah email dan password tidak kosong
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Melakukan sign in dengan email dan password
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        user?.let {
                            if (it.isEmailVerified) {
                                val userId = it.uid
                                // Mengambil data user dari Firestore
                                firestore.collection("users").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            val userData = document.toObject(User::class.java)
                                            // Cek apakah user memiliki role 'user'
                                            if (userData?.role == "user") {
                                                val name = userData.name
                                                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this, Home::class.java)
                                                intent.putExtra("USER_NAME", name)
                                                startActivity(intent)
                                            } else {
                                                // Menampilkan dialog jika role bukan 'user'
                                                NotificationDialog.showDialog(this, "Access Denied", "Only users with the 'user' role can log in.")
                                                firebaseAuth.signOut()
                                            }
                                        } else {
                                            // Menampilkan dialog jika data user tidak ditemukan
                                            NotificationDialog.showDialog(this, "Error", "Data user tidak ditemukan")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        // Menampilkan dialog jika gagal mengambil data user
                                        Log.e("FirestoreError", "Failed to get user data: ", e)
                                        NotificationDialog.showDialog(this, "Error", "User data not found")
                                    }
                            } else {
                                // Menampilkan dialog jika email belum diverifikasi
                                NotificationDialog.showDialog(this, "Email not verified", "Please check your email for verification.")
                            }
                        }
                    } else {
                        // Menampilkan dialog jika login gagal
                        NotificationDialog.showDialog(this, "Login Failed", "Please check your email/password")
                    }
                }
            } else {
                // Menampilkan dialog jika ada field yang kosong
                NotificationDialog.showDialog(this, "Error", "Empty Fields Are not Allowed !!")
            }
        }

        // Set clickable span untuk teks "Forgot Password?"
        val forgotPasswordTextView = findViewById<TextView>(R.id.textViewForgot)
        val text = "Forgot Password?"
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@SignIn, ForgotPassword::class.java)
                startActivity(intent)
            }
        }
        spannableString.setSpan(clickableSpan, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        forgotPasswordTextView.text = spannableString
        forgotPasswordTextView.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        // Set clickable span untuk teks "Don’t have accounts? Register now!"
        val textView = findViewById<TextView>(R.id.textView7)
        val text2 = "Don’t have accounts? Register now! "
        val spannableString2 = SpannableString(text2)
        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@SignIn, SignUp::class.java)
                startActivity(intent)
            }
        }
        spannableString2.setSpan(clickableSpan2, 21, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString2
        textView.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }
}