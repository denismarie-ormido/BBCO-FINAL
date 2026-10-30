package com.example.kayara

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class SuperAdminLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_super_admin_login)

        auth = FirebaseAuth.getInstance()

        val etUsername =
            findViewById<EditText>(R.id.etUsername)

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        val tvError =
            findViewById<TextView>(R.id.tvError)

        val btnLogin =
            findViewById<LinearLayout>(R.id.btnLogin)

        val btnBack =
            findViewById<ImageView>(R.id.btnBack)

        val btnTogglePassword =
            findViewById<ImageView>(R.id.btnTogglePassword)

        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener {
            finish()
        }

        // =====================================================
        // SHOW / HIDE PASSWORD
        // =====================================================

        btnTogglePassword.setOnClickListener {

            isPasswordVisible =
                !isPasswordVisible

            if (isPasswordVisible) {

                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                btnTogglePassword.setImageResource(
                    R.drawable.ic_eye_off
                )

            } else {

                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD

                btnTogglePassword.setImageResource(
                    R.drawable.ic_eye
                )
            }

            etPassword.setSelection(
                etPassword.text.length
            )
        }

        // =====================================================
        // LOGIN
        // =====================================================

        btnLogin.setOnClickListener {

            val username =
                etUsername.text
                    .toString()
                    .trim()

            val password =
                etPassword.text
                    .toString()
                    .trim()

            tvError.visibility =
                TextView.GONE

            // =================================================
            // EMPTY FIELD CHECK
            // =================================================

            if (
                username.isEmpty() ||
                password.isEmpty()
            ) {

                showError(
                    tvError,
                    "Please fill in all fields."
                )

                return@setOnClickListener
            }

            // =================================================
            // USERNAME CHECK
            // =================================================

            if (username != "superadmin") {

                showError(
                    tvError,
                    "Invalid username or password."
                )

                return@setOnClickListener
            }

            // =================================================
            // NETWORK CHECK
            // =================================================

            if (!isNetworkAvailable()) {

                showError(
                    tvError,
                    "No network connection. Please check your network connection."
                )

                return@setOnClickListener
            }

            // =================================================
            // DISABLE LOGIN BUTTON
            // =================================================

            btnLogin.isEnabled =
                false

            btnLogin.alpha =
                0.6f

            // =================================================
            // FIREBASE AUTHENTICATION
            // =================================================

            auth.signInWithEmailAndPassword(
                "superadmin@gmail.com",
                password
            )
                .addOnSuccessListener {

                    btnLogin.isEnabled =
                        true

                    btnLogin.alpha =
                        1f

                    Toast.makeText(
                        this,
                        "Welcome Super Admin!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent =
                        Intent(
                            this,
                            SuperAdminDashboardActivity::class.java
                        )

                    startActivity(intent)

                    finish()
                }
                .addOnFailureListener { error ->

                    btnLogin.isEnabled =
                        true

                    btnLogin.alpha =
                        1f

                    val message =
                        getLoginErrorMessage(error)

                    showError(
                        tvError,
                        message
                    )
                }
        }
    }

    // =========================================================
    // NETWORK CHECK
    // =========================================================

    private fun isNetworkAvailable(): Boolean {

        val connectivityManager =
            getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

        val network =
            connectivityManager.activeNetwork
                ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(network)
                ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    // =========================================================
    // FIREBASE LOGIN ERROR TRAPPING
    // =========================================================

    private fun getLoginErrorMessage(
        error: Exception
    ): String {

        if (!isNetworkAvailable()) {

            return "No network connection. Please check your network connection."
        }

        return when (error) {

            is FirebaseNetworkException -> {
                "No network connection. Please check your network connection."
            }

            is FirebaseAuthInvalidCredentialsException -> {
                "Invalid username or password."
            }

            is FirebaseAuthInvalidUserException -> {
                "Super Admin account was not found."
            }

            else -> {
                "Login failed. Please try again."
            }
        }
    }

    // =========================================================
    // ERROR
    // =========================================================

    private fun showError(
        tvError: TextView,
        message: String
    ) {

        tvError.text =
            message

        tvError.visibility =
            TextView.VISIBLE
    }
}