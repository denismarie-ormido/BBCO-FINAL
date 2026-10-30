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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class StudentLoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_student_login
        )


        // =====================================================
        // FIREBASE
        // =====================================================

        auth =
            FirebaseAuth.getInstance()

        db =
            FirebaseFirestore.getInstance()


        // =====================================================
        // UI
        // =====================================================

        val etUsername =
            findViewById<EditText>(
                R.id.etUsername
            )

        val etPassword =
            findViewById<EditText>(
                R.id.etPassword
            )

        val tvError =
            findViewById<TextView>(
                R.id.tvError
            )

        val btnLogin =
            findViewById<LinearLayout>(
                R.id.btnLogin
            )

        val btnBack =
            findViewById<ImageView>(
                R.id.btnBack
            )

        val btnTogglePassword =
            findViewById<ImageView>(
                R.id.btnTogglePassword
            )

        val tvGoToSignUp =
            findViewById<TextView>(
                R.id.tvGoToSignUp
            )


        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {

            finish()
        }


        // =====================================================
        // GO TO SIGN UP
        // =====================================================

        tvGoToSignUp.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    StudentSignUpActivity::class.java
                )
            )
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

            val email =
                etUsername.text
                    .toString()
                    .trim()
                    .lowercase()

            val password =
                etPassword.text
                    .toString()


            tvError.visibility =
                TextView.GONE


            // =================================================
            // EMAIL EMPTY
            // =================================================

            if (
                email.isEmpty()
            ) {

                showError(
                    tvError,
                    "Please enter your BISU student email."
                )

                etUsername.requestFocus()

                return@setOnClickListener
            }


            // =================================================
            // PASSWORD EMPTY
            // =================================================

            if (
                password.isEmpty()
            ) {

                showError(
                    tvError,
                    "Please enter your password."
                )

                etPassword.requestFocus()

                return@setOnClickListener
            }


            // =================================================
            // STRICT STUDENT BISU EMAIL FORMAT
            //
            // REQUIRED FORMAT:
            // bal_schoolID@bisu.edu.ph
            //
            // EXAMPLE:
            // bal_203892@bisu.edu.ph
            //
            // REJECTED:
            // cathy@bisu.edu.ph
            // 203892@bisu.edu.ph
            // bal_cathy@bisu.edu.ph
            // =================================================

            val studentEmailPattern =
                Regex(
                    "^bal_\\d+@bisu\\.edu\\.ph$",
                    RegexOption.IGNORE_CASE
                )


            if (
                !studentEmailPattern.matches(
                    email
                )
            ) {

                showError(
                    tvError,
                    "Invalid student email format. Use bal_schoolID@bisu.edu.ph, for example bal_203892@bisu.edu.ph."
                )

                etUsername.requestFocus()

                return@setOnClickListener
            }


            // =================================================
            // NETWORK CHECK
            // =================================================

            if (
                !isNetworkAvailable()
            ) {

                showError(
                    tvError,
                    "No network connection. Please check your network connection."
                )

                return@setOnClickListener
            }


            // =================================================
            // DISABLE BUTTON
            // =================================================

            btnLogin.isEnabled =
                false

            btnLogin.alpha =
                0.6f


            // =================================================
            // FIREBASE AUTHENTICATION
            // =================================================

            auth.signInWithEmailAndPassword(
                email,
                password
            )
                .addOnSuccessListener {


                    val currentUser =
                        auth.currentUser


                    if (
                        currentUser ==
                        null
                    ) {

                        enableLoginButton(
                            btnLogin
                        )

                        showError(
                            tvError,
                            "Login failed. Please try again."
                        )

                        return@addOnSuccessListener
                    }


                    // =========================================
                    // NETWORK MAY DISAPPEAR AFTER AUTH
                    // =========================================

                    if (
                        !isNetworkAvailable()
                    ) {

                        enableLoginButton(
                            btnLogin
                        )

                        showError(
                            tvError,
                            "No network connection. Please check your network connection."
                        )

                        return@addOnSuccessListener
                    }


                    val uid =
                        currentUser.uid


                    // =========================================
                    // GET STUDENT INFORMATION
                    // =========================================

                    db.collection(
                        "users"
                    )
                        .document(
                            uid
                        )
                        .get()
                        .addOnSuccessListener {
                                document ->


                            enableLoginButton(
                                btnLogin
                            )


                            if (
                                document.exists()
                            ) {

                                val role =
                                    document.getString(
                                        "role"
                                    )


                                if (
                                    role ==
                                    "student"
                                ) {

                                    // =================================
                                    // EXTRA SECURITY CHECK:
                                    // FIRESTORE EMAIL SHOULD ALSO USE
                                    // STUDENT FORMAT
                                    // =================================

                                    val savedEmail =
                                        document
                                            .getString(
                                                "email"
                                            )
                                            ?.trim()
                                            ?.lowercase()
                                            ?: ""


                                    if (
                                        savedEmail.isNotEmpty() &&
                                        !studentEmailPattern.matches(
                                            savedEmail
                                        )
                                    ) {

                                        auth.signOut()

                                        showError(
                                            tvError,
                                            "This account does not use a valid BISU student email."
                                        )

                                        return@addOnSuccessListener
                                    }


                                    Toast.makeText(
                                        this,
                                        "Welcome back!",
                                        Toast.LENGTH_SHORT
                                    ).show()


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
                                error ->


                            enableLoginButton(
                                btnLogin
                            )


                            showError(
                                tvError,
                                getFirestoreErrorMessage(
                                    error,
                                    "Unable to load your account information."
                                )
                            )
                        }
                }
                .addOnFailureListener {
                        error ->


                    enableLoginButton(
                        btnLogin
                    )


                    showError(
                        tvError,
                        getLoginErrorMessage(
                            error
                        )
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
            connectivityManager
                .getNetworkCapabilities(
                    network
                )
                ?: return false


        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }


    // =========================================================
    // FIREBASE AUTH ERROR
    // =========================================================

    private fun getLoginErrorMessage(
        error: Exception
    ): String {

        if (
            !isNetworkAvailable()
        ) {

            return "No network connection. Please check your network connection."
        }


        return when (
            error
        ) {

            is FirebaseNetworkException -> {

                "No network connection. Please check your network connection."
            }


            is FirebaseAuthInvalidCredentialsException -> {

                "Invalid email or password."
            }


            is FirebaseAuthInvalidUserException -> {

                "Student account was not found."
            }


            else -> {

                "Login failed. Please try again."
            }
        }
    }


    // =========================================================
    // FIRESTORE ERROR
    // =========================================================

    private fun getFirestoreErrorMessage(
        error: Exception,
        defaultMessage: String
    ): String {

        if (
            !isNetworkAvailable()
        ) {

            return "No network connection. Please check your network connection."
        }


        if (
            error is
                    FirebaseFirestoreException
        ) {

            if (
                error.code ==
                FirebaseFirestoreException.Code.UNAVAILABLE ||
                error.code ==
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED
            ) {

                return "No network connection. Please check your network connection."
            }
        }


        return defaultMessage
    }


    // =========================================================
    // ENABLE LOGIN BUTTON
    // =========================================================

    private fun enableLoginButton(
        btnLogin: LinearLayout
    ) {

        btnLogin.isEnabled =
            true

        btnLogin.alpha =
            1f
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