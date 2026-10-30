package com.example.kayara

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class AdminLoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvError: TextView
    private lateinit var btnLogin: LinearLayout
    private lateinit var btnEditResubmit: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin_login)

        auth =
            FirebaseAuth.getInstance()

        db =
            FirebaseFirestore.getInstance()

        etUsername =
            findViewById(R.id.etUsername)

        etPassword =
            findViewById(R.id.etPassword)

        tvError =
            findViewById(R.id.tvError)

        btnLogin =
            findViewById(R.id.btnLogin)

        btnEditResubmit =
            findViewById(R.id.btnEditResubmit)

        val btnBack =
            findViewById<ImageView>(R.id.btnBack)

        val btnTogglePassword =
            findViewById<ImageView>(R.id.btnTogglePassword)

        val tvGoToSignUp =
            findViewById<TextView>(R.id.tvGoToSignUp)

        // Initial state
        btnEditResubmit.visibility =
            View.GONE

        tvError.visibility =
            View.GONE

        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {
            finish()
        }

        // =====================================================
        // SIGN UP
        // =====================================================

        tvGoToSignUp.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AdminSignUpActivity::class.java
                )
            )
        }

        // =====================================================
        // EDIT & RESUBMIT
        // =====================================================

        // =====================================================
// EDIT & RESUBMIT
// =====================================================

        btnEditResubmit.setOnClickListener {

            val username =
                etUsername.text
                    .toString()
                    .trim()
                    .lowercase()

            val password =
                etPassword.text
                    .toString()

            if (username.isBlank()) {

                showError(
                    "Please enter your organization username first."
                )

                return@setOnClickListener
            }

            if (password.isBlank()) {

                showError(
                    "Please enter your password first."
                )

                return@setOnClickListener
            }

            btnEditResubmit.isEnabled = false

            btnEditResubmit.text =
                "Opening..."


            // =================================================
            // FIND ADMIN ACCOUNT USING USERNAME
            // =================================================

            db.collection("users")
                .whereEqualTo(
                    "username",
                    username
                )
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->

                    if (snapshot.isEmpty) {

                        btnEditResubmit.isEnabled = true

                        btnEditResubmit.text =
                            "Edit & Resubmit Application"

                        showError(
                            "Unable to find organization account."
                        )

                        return@addOnSuccessListener
                    }


                    val document =
                        snapshot.documents.first()


                    val uid =
                        document.id


                    val email =
                        document.getString(
                            "email"
                        )
                            ?.trim()
                            ?: ""


                    val role =
                        document.getString(
                            "role"
                        )
                            ?.trim()
                            ?.lowercase()
                            ?: ""


                    val status =
                        document.getString(
                            "status"
                        )
                            ?.trim()
                            ?.lowercase()
                            ?: ""


                    // =================================================
                    // VALIDATE ACCOUNT
                    // =================================================

                    if (role != "admin") {

                        btnEditResubmit.isEnabled = true

                        btnEditResubmit.text =
                            "Edit & Resubmit Application"

                        showError(
                            "This account is not an organization admin."
                        )

                        return@addOnSuccessListener
                    }


                    if (status != "rejected") {

                        btnEditResubmit.isEnabled = true

                        btnEditResubmit.text =
                            "Edit & Resubmit Application"

                        showError(
                            "This application is not available for resubmission."
                        )

                        return@addOnSuccessListener
                    }


                    if (email.isBlank()) {

                        btnEditResubmit.isEnabled = true

                        btnEditResubmit.text =
                            "Edit & Resubmit Application"

                        showError(
                            "Account email was not found."
                        )

                        return@addOnSuccessListener
                    }


                    // =================================================
                    // AUTHENTICATE USING REAL EMAIL
                    // =================================================

                    auth.signInWithEmailAndPassword(
                        email,
                        password
                    )
                        .addOnSuccessListener {

                            val firebaseUser =
                                auth.currentUser

                            if (firebaseUser == null) {

                                btnEditResubmit.isEnabled =
                                    true

                                btnEditResubmit.text =
                                    "Edit & Resubmit Application"

                                showError(
                                    "Unable to verify account."
                                )

                                return@addOnSuccessListener
                            }


                            // =================================================
                            // LOAD REJECTED APPLICATION
                            // =================================================

                            getRejectedApplication(
                                firebaseUser.uid
                            )
                        }
                        .addOnFailureListener { error ->

                            btnEditResubmit.isEnabled =
                                true

                            btnEditResubmit.text =
                                "Edit & Resubmit Application"

                            showError(
                                "Unable to verify account: ${error.message}"
                            )
                        }
                }
                .addOnFailureListener { error ->

                    btnEditResubmit.isEnabled =
                        true

                    btnEditResubmit.text =
                        "Edit & Resubmit Application"

                    showError(
                        "Unable to find organization account: ${error.message}"
                    )
                }
        }

        // =====================================================
        // PASSWORD SHOW / HIDE
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
                    .lowercase()

            val password =
                etPassword.text
                    .toString()

            btnEditResubmit.visibility =
                View.GONE

            tvError.visibility =
                View.GONE

            // =============================================
            // EMPTY FIELD CHECK
            // =============================================

            if (
                username.isEmpty() ||
                password.isEmpty()
            ) {

                showError(
                    "Please enter your organization username and password."
                )

                return@setOnClickListener
            }

            // =============================================
            // NETWORK CHECK
            // =============================================

            if (!isNetworkAvailable()) {

                showError(
                    "No network connection. Please check your network connection."
                )

                return@setOnClickListener
            }

            btnLogin.isEnabled =
                false

            btnLogin.alpha =
                0.6f

            // =============================================
            // USERNAME -> FIREBASE AUTH EMAIL
            // =============================================

            findAdminEmailByUsername(
                username = username,

                onFound = { email ->

                    authenticateAdmin(
                        email = email,
                        password = password
                    )
                },

                onNotFound = {

                    enableLoginButton()

                    showError(
                        "Organization username was not found."
                    )
                }
            )
        }
    }


    // =========================================================
    // FIND ADMIN EMAIL USING ORGANIZATION USERNAME
    // =========================================================

    private fun findAdminEmailByUsername(
        username: String,
        onFound: (String) -> Unit,
        onNotFound: () -> Unit
    ) {

        db.collection("users")
            .whereEqualTo(
                "username",
                username
            )
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.isEmpty) {

                    onNotFound()

                    return@addOnSuccessListener
                }

                val document =
                    snapshot.documents.first()

                val role =
                    document.getString("role")
                        ?.trim()
                        ?.lowercase()
                        ?: ""

                if (role != "admin") {

                    enableLoginButton()

                    showError(
                        "This username is not an organization admin account."
                    )

                    return@addOnSuccessListener
                }

                val email =
                    document.getString("email")
                        ?.trim()
                        ?.lowercase()
                        ?: ""

                if (email.isBlank()) {

                    enableLoginButton()

                    showError(
                        "The organization account has no valid login email."
                    )

                    return@addOnSuccessListener
                }

                onFound(email)
            }
            .addOnFailureListener { error ->

                enableLoginButton()

                showError(
                    getFirestoreErrorMessage(
                        error,
                        "Unable to find organization account."
                    )
                )
            }
    }


    // =========================================================
    // AUTHENTICATE ADMIN WITH FIREBASE EMAIL/PASSWORD
    // =========================================================

    private fun authenticateAdmin(
        email: String,
        password: String
    ) {

        auth.signInWithEmailAndPassword(
            email,
            password
        )
            .addOnSuccessListener {

                val firebaseUser =
                    auth.currentUser

                if (firebaseUser == null) {

                    enableLoginButton()

                    showError(
                        "Unable to get account information."
                    )

                    return@addOnSuccessListener
                }

                if (!isNetworkAvailable()) {

                    enableLoginButton()

                    showError(
                        "No network connection. Please check your network connection."
                    )

                    return@addOnSuccessListener
                }

                loadAdminProfile(
                    firebaseUser.uid
                )
            }
            .addOnFailureListener { error ->

                enableLoginButton()

                showError(
                    getLoginErrorMessage(error)
                )
            }
    }


    // =========================================================
    // GET REJECTED APPLICATION
    // =========================================================

    private fun getRejectedApplication(
        uid: String
    ) {

        if (!isNetworkAvailable()) {

            resetResubmitButton()

            showError(
                "No network connection. Please check your network connection."
            )

            return
        }

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    resetResubmitButton()

                    auth.signOut()

                    showError(
                        "Admin profile not found."
                    )

                    return@addOnSuccessListener
                }

                val role =
                    document.getString("role")
                        ?: ""

                val status =
                    document.getString("status")
                        ?: ""

                val applicationId =
                    document.getString("applicationId")
                        ?: ""

                if (role != "admin") {

                    resetResubmitButton()

                    auth.signOut()

                    showError(
                        "This account is not an organization admin."
                    )

                    return@addOnSuccessListener
                }

                if (status != "rejected") {

                    resetResubmitButton()

                    auth.signOut()

                    showError(
                        "This application is not available for resubmission."
                    )

                    return@addOnSuccessListener
                }

                if (applicationId.isBlank()) {

                    resetResubmitButton()

                    auth.signOut()

                    showError(
                        "Application information was not found."
                    )

                    return@addOnSuccessListener
                }

                // IMPORTANT:
                // DO NOT SIGN OUT HERE.
                // AdminResubmitActivity needs authenticated Firebase user.

                val intent =
                    Intent(
                        this,
                        AdminResubmitActivity::class.java
                    )

                intent.putExtra(
                    "applicationId",
                    applicationId
                )

                intent.putExtra(
                    "uid",
                    uid
                )

                startActivity(intent)

                resetResubmitButton()
            }
            .addOnFailureListener { error ->

                resetResubmitButton()

                auth.signOut()

                showError(
                    getFirestoreErrorMessage(
                        error,
                        "Unable to find application."
                    )
                )
            }
    }

    // =========================================================
    // LOAD ADMIN PROFILE
    // =========================================================

    private fun loadAdminProfile(
        uid: String
    ) {

        if (!isNetworkAvailable()) {

            enableLoginButton()

            showError(
                "No network connection. Please check your network connection."
            )

            return
        }

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {

                    enableLoginButton()

                    auth.signOut()

                    showError(
                        "Admin profile not found."
                    )

                    return@addOnSuccessListener
                }

                val role =
                    document.getString("role")
                        ?: ""

                val status =
                    document.getString("status")
                        ?: ""

                val organizationId =
                    document.getString("organizationId")
                        ?: ""

                val organizationName =
                    document.getString("organizationName")
                        ?: ""

                val rejectionReason =
                    document.getString("rejectionReason")
                        ?: ""

                if (role != "admin") {

                    enableLoginButton()

                    auth.signOut()

                    showError(
                        "This account is not an organization admin."
                    )

                    return@addOnSuccessListener
                }

                when (status) {

                    "pending" -> {

                        enableLoginButton()

                        auth.signOut()

                        btnEditResubmit.visibility =
                            View.GONE

                        showError(
                            "Your organization registration is still pending Super Admin approval."
                        )

                        return@addOnSuccessListener
                    }

                    "rejected" -> {

                        enableLoginButton()

                        auth.signOut()

                        val message =
                            if (rejectionReason.isNotBlank()) {

                                "Your organization registration was rejected.\n\n" +
                                        "Reason:\n" +
                                        rejectionReason

                            } else {

                                "Your organization registration was rejected."
                            }

                        showError(
                            message
                        )

                        btnEditResubmit.visibility =
                            View.VISIBLE

                        return@addOnSuccessListener
                    }

                    "approved" -> {

                        btnEditResubmit.visibility =
                            View.GONE
                    }

                    else -> {

                        enableLoginButton()

                        auth.signOut()

                        btnEditResubmit.visibility =
                            View.GONE

                        showError(
                            "Your account has not been approved yet."
                        )

                        return@addOnSuccessListener
                    }
                }

                if (organizationId.isBlank()) {

                    enableLoginButton()

                    auth.signOut()

                    showError(
                        "No organization is assigned to this admin."
                    )

                    return@addOnSuccessListener
                }

                enableLoginButton()

                val intent =
                    Intent(
                        this,
                        AdminDashboardActivity::class.java
                    )

                intent.putExtra(
                    "organizationId",
                    organizationId
                )

                intent.putExtra(
                    "organizationName",
                    organizationName
                )

                startActivity(intent)

                finish()
            }
            .addOnFailureListener { error ->

                enableLoginButton()

                auth.signOut()

                btnEditResubmit.visibility =
                    View.GONE

                showError(
                    getFirestoreErrorMessage(
                        error,
                        "Failed to load admin profile."
                    )
                )
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
                .getNetworkCapabilities(network)
                ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    // =========================================================
    // AUTHENTICATION ERROR
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
                "Invalid organization username or password."
            }

            is FirebaseAuthInvalidUserException -> {
                "Admin account was not found."
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

        if (!isNetworkAvailable()) {

            return "No network connection. Please check your network connection."
        }

        if (error is FirebaseFirestoreException) {

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

    private fun enableLoginButton() {

        btnLogin.isEnabled =
            true

        btnLogin.alpha =
            1f
    }

    // =========================================================
    // RESET RESUBMIT BUTTON
    // =========================================================

    private fun resetResubmitButton() {

        btnEditResubmit.isEnabled =
            true

        btnEditResubmit.text =
            "Edit & Resubmit Application"
    }

    // =========================================================
    // ERROR
    // =========================================================

    private fun showError(
        message: String
    ) {

        tvError.text =
            message

        tvError.visibility =
            View.VISIBLE
    }
}
