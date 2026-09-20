package com.example.kayara

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentSignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var isRegistering = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_sign_up)

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Views
        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etStudentId = findViewById<EditText>(R.id.etStudentId)
        val spinnerCourse = findViewById<Spinner>(R.id.spinnerCourse)
        val spinnerYearLevel = findViewById<Spinner>(R.id.spinnerYearLevel)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val etInterest = findViewById<EditText>(R.id.etInterest)

        val tvError = findViewById<TextView>(R.id.tvError)
        val btnSignUp = findViewById<LinearLayout>(R.id.btnSignUp)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        val btnTogglePassword =
            findViewById<ImageView>(R.id.btnTogglePassword)

        val btnToggleConfirmPassword =
            findViewById<ImageView>(R.id.btnToggleConfirmPassword)

        // ---------------------------------------------------------
        // BACK
        // ---------------------------------------------------------

        btnBack.setOnClickListener {
            finish()
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }

        // ---------------------------------------------------------
        // PASSWORD VISIBILITY
        // ---------------------------------------------------------

        btnTogglePassword.setOnClickListener {

            isPasswordVisible = !isPasswordVisible

            togglePasswordVisibility(
                etPassword,
                btnTogglePassword,
                isPasswordVisible
            )
        }

        btnToggleConfirmPassword.setOnClickListener {

            isConfirmPasswordVisible = !isConfirmPasswordVisible

            togglePasswordVisibility(
                etConfirmPassword,
                btnToggleConfirmPassword,
                isConfirmPasswordVisible
            )
        }

        // ---------------------------------------------------------
        // SIGN UP
        // ---------------------------------------------------------

        btnSignUp.setOnClickListener {

            if (isRegistering) {
                return@setOnClickListener
            }

            val fullName = etFullName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()

            val course = spinnerCourse.selectedItem?.toString()?.trim() ?: ""
            val yearLevel =
                spinnerYearLevel.selectedItem?.toString()?.trim() ?: ""

            val email = etEmail.text.toString().trim().lowercase()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val interest = etInterest.text.toString().trim()

            // -----------------------------------------------------
            // VALIDATION
            // -----------------------------------------------------

            if (
                fullName.isEmpty() ||
                studentId.isEmpty() ||
                email.isEmpty() ||
                username.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty() ||
                interest.isEmpty()
            ) {

                showError(
                    tvError,
                    "Please fill in all fields."
                )

                return@setOnClickListener
            }

            if (course == "Select Course") {

                showError(
                    tvError,
                    "Please select your course."
                )

                return@setOnClickListener
            }

            if (yearLevel == "Select Year Level") {

                showError(
                    tvError,
                    "Please select your year level."
                )

                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                showError(
                    tvError,
                    "Please enter a valid email address."
                )

                return@setOnClickListener
            }

            if (!email.endsWith("@bisu.edu.ph")) {

                showError(
                    tvError,
                    "Please use your official BISU email (@bisu.edu.ph)."
                )

                return@setOnClickListener
            }

            if (password.length < 6) {

                showError(
                    tvError,
                    "Password must be at least 6 characters."
                )

                return@setOnClickListener
            }

            if (password != confirmPassword) {

                showError(
                    tvError,
                    "Passwords do not match."
                )

                return@setOnClickListener
            }

            // -----------------------------------------------------
            // START REGISTRATION
            // -----------------------------------------------------

            isRegistering = true

            btnSignUp.isEnabled = false
            btnSignUp.alpha = 0.6f

            tvError.visibility = View.GONE

            // -----------------------------------------------------
            // FIREBASE AUTHENTICATION
            // -----------------------------------------------------

            auth.createUserWithEmailAndPassword(
                email,
                password
            ).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val firebaseUser = auth.currentUser

                    if (firebaseUser == null) {

                        resetSignUpButton(btnSignUp)

                        showError(
                            tvError,
                            "Account creation failed. Please try again."
                        )

                        return@addOnCompleteListener
                    }

                    val uid = firebaseUser.uid

                    // -------------------------------------------------
                    // STUDENT DATA FOR FIRESTORE
                    // -------------------------------------------------

                    val studentData = hashMapOf(
                        "uid" to uid,
                        "fullName" to fullName,
                        "studentId" to studentId,
                        "course" to course,
                        "yearLevel" to yearLevel,
                        "email" to email,
                        "username" to username,
                        "interest" to interest,
                        "role" to "student",
                        "createdAt" to System.currentTimeMillis()
                    )

                    // -------------------------------------------------
                    // SAVE STUDENT DATA
                    // -------------------------------------------------

                    db.collection("users")
                        .document(uid)
                        .set(studentData)
                        .addOnSuccessListener {

                            resetSignUpButton(btnSignUp)

                            Toast.makeText(
                                this,
                                "Account created successfully!",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                        }
                        .addOnFailureListener { error ->

                            resetSignUpButton(btnSignUp)

                            showError(
                                tvError,
                                "Account created, but student information could not be saved. Please try again."
                            )

                            Toast.makeText(
                                this,
                                "Database error: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {

                    resetSignUpButton(btnSignUp)

                    val message = when {

                        task.exception?.message?.contains(
                            "already in use",
                            ignoreCase = true
                        ) == true -> {
                            "This email is already registered."
                        }

                        task.exception?.message?.contains(
                            "badly formatted",
                            ignoreCase = true
                        ) == true -> {
                            "Invalid email address."
                        }

                        task.exception?.message?.contains(
                            "network",
                            ignoreCase = true
                        ) == true -> {
                            "Network error. Please check your internet connection."
                        }

                        else -> {
                            "Unable to create account. Please try again."
                        }
                    }

                    showError(tvError, message)
                }
            }
        }
    }

    // -------------------------------------------------------------
    // PASSWORD VISIBILITY
    // -------------------------------------------------------------

    private fun togglePasswordVisibility(
        editText: EditText,
        icon: ImageView,
        isVisible: Boolean
    ) {

        if (isVisible) {

            editText.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            icon.setImageResource(R.drawable.ic_eye_off)

        } else {

            editText.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD

            icon.setImageResource(R.drawable.ic_eye)
        }

        editText.setSelection(editText.text.length)
    }

    // -------------------------------------------------------------
    // SHOW ERROR
    // -------------------------------------------------------------

    private fun showError(
        tvError: TextView,
        message: String
    ) {

        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    // -------------------------------------------------------------
    // RESET SIGN UP BUTTON
    // -------------------------------------------------------------

    private fun resetSignUpButton(
        btnSignUp: LinearLayout
    ) {

        isRegistering = false

        btnSignUp.isEnabled = true
        btnSignUp.alpha = 1.0f
    }
}