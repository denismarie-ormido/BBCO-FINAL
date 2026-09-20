package com.example.kayara

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class AdminSignUpActivity : AppCompatActivity() {

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var selectedFileUri: Uri? = null

    private val pickDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedFileUri = uri
                val fileName = getFileNameFromUri(uri)
                findViewById<TextView>(R.id.tvSelectedFile).text = fileName
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_sign_up)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etOrgName = findViewById<EditText>(R.id.etOrgName)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val tvError = findViewById<TextView>(R.id.tvError)
        val btnSignUp = findViewById<LinearLayout>(R.id.btnSignUp)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)
        val uploadCard = findViewById<LinearLayout>(R.id.uploadCard)
        val btnTogglePassword = findViewById<ImageView>(R.id.btnTogglePassword)
        val btnToggleConfirmPassword = findViewById<ImageView>(R.id.btnToggleConfirmPassword)

        btnBack.setOnClickListener { finish() }
        tvGoToLogin.setOnClickListener { finish() }

        uploadCard.setOnClickListener {
            pickDocumentLauncher.launch("*/*")
        }

        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(etPassword, btnTogglePassword, isPasswordVisible)
        }

        btnToggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(etConfirmPassword, btnToggleConfirmPassword, isConfirmPasswordVisible)
        }

        btnSignUp.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val orgName = etOrgName.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (fullName.isEmpty() || orgName.isEmpty() || description.isEmpty() ||
                username.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty()
            ) {
                showError(tvError, "Please fill in all fields")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError(tvError, "Please enter a valid email address")
                return@setOnClickListener
            }

            if (password.length < 6) {
                showError(tvError, "Password must be at least 6 characters")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showError(tvError, "Passwords do not match")
                return@setOnClickListener
            }

            if (selectedFileUri == null) {
                showError(tvError, "Please upload your requirement document")
                return@setOnClickListener
            }

            // TODO: I-upload ang fields + selectedFileUri sa server/database
            // Status = "pending" hangtod i-approve sa Super Admin

            tvError.visibility = TextView.GONE
            Toast.makeText(
                this,
                "Registration submitted! Please wait for Super Admin approval.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun togglePasswordVisibility(editText: EditText, icon: ImageView, isVisible: Boolean) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            icon.setImageResource(R.drawable.ic_eye_off)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            icon.setImageResource(R.drawable.ic_eye)
        }
        editText.setSelection(editText.text.length)
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var name = "Selected document"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) name = it.getString(nameIndex)
            }
        }
        return name
    }

    private fun showError(tvError: TextView, message: String) {
        tvError.text = message
        tvError.visibility = TextView.VISIBLE
    }
}