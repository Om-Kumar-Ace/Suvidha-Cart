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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var nameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var phoneEt: EditText
    private lateinit var passEt: EditText
    private lateinit var signUpButton: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        nameEt = findViewById(R.id.textInputEditNama)
        emailEt = findViewById(R.id.textInputEditEmail)
        phoneEt = findViewById(R.id.textInputEditPhone)
        passEt = findViewById(R.id.editTextPassword2)
        signUpButton = findViewById(R.id.buttonSignup)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        signUpButton.setOnClickListener {
            val name = nameEt.text.toString()
            val email = emailEt.text.toString()
            val phone = phoneEt.text.toString()
            val pass = passEt.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && pass.isNotEmpty()) {
                if (pass.length >= 8) {
                    registerUser(name, email, phone, pass)
                } else {
                    showNotification("Error", "Password must be at least 8 characters")
                }
            } else {
                showNotification("Error", "Empty Fields Are Not Allowed !!")
            }
        }

        val textView = findViewById<TextView>(R.id.signin)
        val text = "Already have an account? Sign in"
        val spannableString = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@SignUp, SignIn::class.java))
            }
        }

        spannableString.setSpan(clickableSpan, 24, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }

    private fun registerUser(name: String, email: String, phone: String, pass: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.let {
                    val userId = it.uid
                    val newUser = User(id = userId, name = name, phone = phone, role = "user")
                    saveUserToFirestore(newUser, user)
                    sendEmailVerification(user)
                }
            } else {
                if (task.exception is FirebaseAuthUserCollisionException) {
                    showNotification("Email already in use", "Please use a different email")
                } else {
                    showNotification("Register Failed", "Try again")
                }
            }
        }
    }

    private fun saveUserToFirestore(user: User, firebaseUser: FirebaseUser) {
        firestore.collection("users").document(user.id).set(user)
            .addOnSuccessListener {
                sendEmailVerification(firebaseUser)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Failed to add user data: ", e)
                firebaseUser.delete()
                showNotification("Register Failed", "Please fill the requirement!")
            }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Verification email sent. Please check your email to LOGIN.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, SignIn::class.java))
                finish()
            } else {
                val errorMessage = task.exception?.localizedMessage ?: "Failed to send verification email."
                Log.e("EmailVerification", errorMessage)
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showNotification(title: String, message: String) {
        NotificationDialog.showDialog(this, title, message)
    }
}
