package com.example.kayara

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentLoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_login)

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // UI
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvError = findViewById<TextView>(R.id.tvError)
        val btnLogin = findViewById<LinearLayout>(R.id.btnLogin)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnTogglePassword = findViewById<ImageView>(R.id.btnTogglePassword)
        val tvGoToSignUp = findViewById<TextView>(R.id.tvGoToSignUp)

        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Go to Sign Up
        tvGoToSignUp.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    StudentSignUpActivity::class.java
                )
            )
        }

        // Show / Hide Password
        btnTogglePassword.setOnClickListener {

            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {

                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                btnTogglePassword.setImageResource(
                    R.drawable.ic_eye
                )

            } else {

                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD

                btnTogglePassword.setImageResource(
                    R.drawable.ic_eye_off
                )
            }

            etPassword.setSelection(
                etPassword.text.length
            )
        }

        // Login
        btnLogin.setOnClickListener {

            val email = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Clear previous error
            tvError.visibility = TextView.GONE

            // Check empty fields
            if (email.isEmpty()) {
                showError(
                    tvError,
                    "Please enter your BISU email."
                )
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                showError(
                    tvError,
                    "Please enter your password."
                )
                return@setOnClickListener
            }

            // Check BISU email
            if (!email.endsWith("@bisu.edu.ph")) {
                showError(
                    tvError,
                    "Please use your official BISU email."
                )
                return@setOnClickListener
            }

            // Disable button while logging in
            btnLogin.isEnabled = false
            btnLogin.alpha = 0.6f

            // Firebase Authentication
            auth.signInWithEmailAndPassword(
                email,
                password
            )
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val currentUser = auth.currentUser

                        if (currentUser == null) {

                            btnLogin.isEnabled = true
                            btnLogin.alpha = 1f

                            showError(
                                tvError,
                                "Login failed. Please try again."
                            )

                            return@addOnCompleteListener
                        }

                        val uid = currentUser.uid

                        // Get student information from Firestore
                        db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { document ->

                                btnLogin.isEnabled = true
                                btnLogin.alpha = 1f

                                if (document.exists()) {

                                    val role =
                                        document.getString("role")

                                    if (role == "student") {

                                        Toast.makeText(
                                            this,
                                            "Welcome back!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Open Student Dashboard
                                        startActivity(
                                            Intent(
                                                this,
                                                StudentDashboardActivity::class.java
                                            )
                                        )

                                        finish()

                                    } else {

                                        auth.signOut()

                                        showError(
                                            tvError,
                                            "This account is not registered as a student."
                                        )
                                    }

                                } else {

                                    auth.signOut()

                                    showError(
                                        tvError,
                                        "Student account information was not found."
                                    )
                                }
                            }

                            .addOnFailureListener {

                                btnLogin.isEnabled = true
                                btnLogin.alpha = 1f

                                showError(
                                    tvError,
                                    "Unable to load your account information."
                                )
                            }

                    } else {

                        btnLogin.isEnabled = true
                        btnLogin.alpha = 1f

                        val errorMessage =
                            when (task.exception?.message) {

                                "The password is invalid or the user does not have a password." ->
                                    "Incorrect password."

                                "The supplied auth credential is incorrect, malformed or has expired." ->
                                    "Invalid email or password."

                                else ->
                                    "Invalid email or password."
                            }

                        showError(
                            tvError,
                            errorMessage
                        )
                    }
                }
        }
    }

    private fun showError(
        tvError: TextView,
        message: String
    ) {
        tvError.text = message
        tvError.visibility = TextView.VISIBLE
    }
}