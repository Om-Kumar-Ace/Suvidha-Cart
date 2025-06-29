package com.example.cart.view

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cart.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject


class Home : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var profileImageView: ImageView
    private lateinit var textView2: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi RecyclerView


        textView2 = findViewById(R.id.textView)
        profileImageView = findViewById(R.id.imageProfile)

       
        fetchProfileImage()
    }

    override fun onResume() {
        super.onResume()
        fetchProfileImage()
    }

    /**
     * Mengambil gambar profil pengguna dari Firestore.
     */
    private fun fetchProfileImage() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val profileImage = document.getString("profileImage")
                        if (profileImage != null && profileImage.isNotEmpty()) {
                            Picasso.get().load(profileImage).into(profileImageView)
                        } else {
                            profileImageView.setImageResource(R.drawable.baseline_person_24)
                        }

                        val userName = document.getString("name")
                        if (userName != null && userName.isNotEmpty()) {
                            textView2.text = "Welcome $userName"
                        } else {
                            textView2.text = "Welcome Guest"
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch profile image: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Firestore", "Error fetching profile image", exception)
                    textView2.text = "Welcome Guest"
                }
        } else {
            profileImageView.setImageResource(R.drawable.baseline_person_24)
            textView2.text = "Welcome Guest"
        }
    }


    fun moveToMyCart(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name")
                        if (userName != null && userName.isNotEmpty()) {
                            textView2.text = "Welcome $userName"
                            val intent = Intent(this, CartActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

        } else {
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            builder.setMessage("You need to login to \n" +
                    "use the features")
                .setCancelable(false)
                .setPositiveButton("Login") { dialog, id ->
                    val loginIntent = Intent(this, SignIn::class.java)
                    startActivity(loginIntent)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    /**
     * Pindah ke aktivitas TranslateVideoBISINDOToText jika pengguna sudah login.
     */
    fun moveToAddItem(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name")
                        if (userName != null && userName.isNotEmpty()) {
                            textView2.text = "Welcome $userName"
                            val intent = Intent(this, SaveCartActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

        } else {
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            builder.setMessage("You need to login to \n" +
                    "use the features")
                .setCancelable(false)
                .setPositiveButton("Login") { dialog, id ->
                    val loginIntent = Intent(this, SignIn::class.java)
                    startActivity(loginIntent)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }


    fun moveToTextToASL(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name")
                        if (userName != null && userName.isNotEmpty()) {
                            textView2.text = "Welcome $userName"
                            val intent = Intent(this, HistoryActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
        } else {
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            builder.setMessage(
                "You need to login to \n" +
                        "use the features"
            )
                .setCancelable(false)
                .setPositiveButton("Login") { dialog, id ->
                    val loginIntent = Intent(this, SignIn::class.java)
                    startActivity(loginIntent)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }


    fun moveHistory(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name")
                        if (userName != null && userName.isNotEmpty()) {
                            textView2.text = "Welcome $userName"
                            val intent = Intent(this, HistoryActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
        } else {
            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            builder.setMessage(
                "You need to login to \n" +
                        "use the features"
            )
                .setCancelable(false)
                .setPositiveButton("Login") { dialog, id ->
                    val loginIntent = Intent(this, SignIn::class.java)
                    startActivity(loginIntent)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }


    fun moveToSignIn(view: View) {

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name")
                        if (userName != null && userName.isNotEmpty()) {
                            textView2.text = "Welcome $userName"
                            val intent = Intent(this, Profile::class.java)
                            startActivity(intent)
                        }
                    }
                }
        } else {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
}
