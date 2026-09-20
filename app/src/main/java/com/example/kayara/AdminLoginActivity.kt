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

class AdminLoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        // -----------------------------------------
        // FIREBASE AUTHENTICATION
        // -----------------------------------------

        auth = FirebaseAuth.getInstance()

        // -----------------------------------------
        // VIEWS
        // -----------------------------------------

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

        val tvGoToSignUp =
            findViewById<TextView>(R.id.tvGoToSignUp)

        // -----------------------------------------
        // BACK
        // -----------------------------------------

        btnBack.setOnClickListener {
            finish()
        }

        // -----------------------------------------
        // SIGN UP
        // -----------------------------------------

        tvGoToSignUp.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AdminSignUpActivity::class.java
                )
            )
        }

        // -----------------------------------------
        // SHOW / HIDE PASSWORD
        // -----------------------------------------

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

        // -----------------------------------------
        // LOGIN
        // -----------------------------------------

        btnLogin.setOnClickListener {

            val username =
                etUsername.text
                    .toString()
                    .trim()

            val password =
                etPassword.text
                    .toString()
                    .trim()

            // -------------------------------------
            // CHECK EMPTY FIELDS
            // -------------------------------------

            if (
                username.isEmpty() ||
                password.isEmpty()
            ) {

                showError(
                    tvError,
                    "Please fill in all fields"
                )

                return@setOnClickListener
            }

            // -------------------------------------
            // ADMIN USERNAME
            // -------------------------------------

            if (username != "admin") {

                showError(
                    tvError,
                    "Invalid username or password"
                )

                return@setOnClickListener
            }

            // -------------------------------------
            // FIREBASE LOGIN
            // -------------------------------------

            val adminEmail =
                "admin@bbco.com"

            btnLogin.isEnabled = false

            tvError.visibility =
                TextView.GONE

            auth.signInWithEmailAndPassword(
                adminEmail,
                password
            )
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Welcome Admin!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // ---------------------------------
                    // OPEN ADMIN DASHBOARD
                    // ---------------------------------

                    startActivity(
                        Intent(
                            this,
                            AdminDashboardActivity::class.java
                        )
                    )

                    finish()
                }
                .addOnFailureListener {

                    btnLogin.isEnabled = true

                    showError(
                        tvError,
                        "Invalid username or password"
                    )
                }
        }
    }

    // =================================================
    // SHOW ERROR
    // =================================================

    private fun showError(
        tvError: TextView,
        message: String
    ) {

        tvError.text = message

        tvError.visibility =
            TextView.VISIBLE
    }
}