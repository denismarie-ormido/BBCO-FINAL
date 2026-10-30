package com.example.kayara

import android.os.Bundle
import android.text.InputType
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

        setContentView(
            R.layout.activity_student_sign_up
        )


        // =====================================================
        // FIREBASE
        // =====================================================

        auth =
            FirebaseAuth.getInstance()

        db =
            FirebaseFirestore.getInstance()


        // =====================================================
        // VIEWS
        // =====================================================

        val etFullName =
            findViewById<EditText>(
                R.id.etFullName
            )

        val etStudentId =
            findViewById<EditText>(
                R.id.etStudentId
            )

        val spinnerCourse =
            findViewById<Spinner>(
                R.id.spinnerCourse
            )

        val spinnerYearLevel =
            findViewById<Spinner>(
                R.id.spinnerYearLevel
            )

        val etEmail =
            findViewById<EditText>(
                R.id.etEmail
            )

        val etUsername =
            findViewById<EditText>(
                R.id.etUsername
            )

        val etPassword =
            findViewById<EditText>(
                R.id.etPassword
            )

        val etConfirmPassword =
            findViewById<EditText>(
                R.id.etConfirmPassword
            )

        val etInterest =
            findViewById<EditText>(
                R.id.etInterest
            )

        val tvError =
            findViewById<TextView>(
                R.id.tvError
            )

        val btnSignUp =
            findViewById<LinearLayout>(
                R.id.btnSignUp
            )

        val btnBack =
            findViewById<ImageView>(
                R.id.btnBack
            )

        val tvGoToLogin =
            findViewById<TextView>(
                R.id.tvGoToLogin
            )

        val btnTogglePassword =
            findViewById<ImageView>(
                R.id.btnTogglePassword
            )

        val btnToggleConfirmPassword =
            findViewById<ImageView>(
                R.id.btnToggleConfirmPassword
            )


        // =====================================================
        // BACK
        // =====================================================

        btnBack.setOnClickListener {

            finish()
        }


        tvGoToLogin.setOnClickListener {

            finish()
        }


        // =====================================================
        // PASSWORD VISIBILITY
        // =====================================================

        btnTogglePassword.setOnClickListener {

            isPasswordVisible =
                !isPasswordVisible

            togglePasswordVisibility(
                etPassword,
                btnTogglePassword,
                isPasswordVisible
            )
        }


        btnToggleConfirmPassword.setOnClickListener {

            isConfirmPasswordVisible =
                !isConfirmPasswordVisible

            togglePasswordVisibility(
                etConfirmPassword,
                btnToggleConfirmPassword,
                isConfirmPasswordVisible
            )
        }


        // =====================================================
        // SIGN UP
        // =====================================================

        btnSignUp.setOnClickListener {

            if (isRegistering) {
                return@setOnClickListener
            }


            // =================================================
            // GET VALUES
            // =================================================

            val fullName =
                etFullName.text
                    .toString()
                    .trim()

            val studentId =
                etStudentId.text
                    .toString()
                    .trim()

            val course =
                spinnerCourse
                    .selectedItem
                    ?.toString()
                    ?.trim()
                    ?: ""

            val yearLevel =
                spinnerYearLevel
                    .selectedItem
                    ?.toString()
                    ?.trim()
                    ?: ""

            val email =
                etEmail.text
                    .toString()
                    .trim()
                    .lowercase()

            val username =
                etUsername.text
                    .toString()
                    .trim()

            val password =
                etPassword.text
                    .toString()

            val confirmPassword =
                etConfirmPassword.text
                    .toString()

            val interest =
                etInterest.text
                    .toString()
                    .trim()


            // =================================================
            // EMPTY FIELD VALIDATION
            // =================================================

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


            // =================================================
            // COURSE VALIDATION
            // =================================================

            if (
                course.equals(
                    "Select Course",
                    ignoreCase = true
                )
            ) {

                showError(
                    tvError,
                    "Please select your course."
                )

                return@setOnClickListener
            }


            // =================================================
            // YEAR LEVEL VALIDATION
            // =================================================

            if (
                yearLevel.equals(
                    "Select Year Level",
                    ignoreCase = true
                )
            ) {

                showError(
                    tvError,
                    "Please select your year level."
                )

                return@setOnClickListener
            }


            // =================================================
            // SCHOOL ID VALIDATION
            //
            // Example:
            // 203892
            // =================================================

            if (!studentId.matches(Regex("^\\d+$"))) {

                showError(
                    tvError,
                    "School ID must contain numbers only."
                )

                etStudentId.requestFocus()

                return@setOnClickListener
            }


            // =================================================
            // STUDENT BISU EMAIL FORMAT
            //
            // REQUIRED:
            // bal_schoolID@bisu.edu.ph
            //
            // Example:
            // bal_203892@bisu.edu.ph
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
                    "Invalid student email. Use this format: bal_schoolID@bisu.edu.ph (Example: bal_203892@bisu.edu.ph)."
                )

                etEmail.requestFocus()

                return@setOnClickListener
            }


            // =================================================
            // EMAIL MUST MATCH SCHOOL ID
            //
            // If ID = 203892
            // email MUST = bal_203892@bisu.edu.ph
            // =================================================

            val expectedEmail =
                "bal_${studentId.lowercase()}@bisu.edu.ph"


            if (
                !email.equals(
                    expectedEmail,
                    ignoreCase = true
                )
            ) {

                showError(
                    tvError,
                    "Your BISU email does not match your School ID. For School ID $studentId, use $expectedEmail."
                )

                etEmail.requestFocus()

                return@setOnClickListener
            }


            // =================================================
            // PASSWORD LENGTH
            // =================================================

            if (
                password.length < 6
            ) {

                showError(
                    tvError,
                    "Password must be at least 6 characters."
                )

                etPassword.requestFocus()

                return@setOnClickListener
            }


            // =================================================
            // PASSWORD MATCH
            // =================================================

            if (
                password !=
                confirmPassword
            ) {

                showError(
                    tvError,
                    "Passwords do not match."
                )

                etConfirmPassword.requestFocus()

                return@setOnClickListener
            }


            // =================================================
            // BEGIN REGISTRATION
            // =================================================

            isRegistering =
                true

            btnSignUp.isEnabled =
                false

            btnSignUp.alpha =
                0.6f

            tvError.visibility =
                View.GONE


            // =================================================
            // FIREBASE AUTH
            // =================================================

            auth.createUserWithEmailAndPassword(
                email,
                password
            )
                .addOnCompleteListener {
                        task ->


                    if (
                        task.isSuccessful
                    ) {

                        val firebaseUser =
                            auth.currentUser


                        if (
                            firebaseUser ==
                            null
                        ) {

                            resetSignUpButton(
                                btnSignUp
                            )

                            showError(
                                tvError,
                                "Account creation failed. Please try again."
                            )

                            return@addOnCompleteListener
                        }


                        val uid =
                            firebaseUser.uid


                        // =====================================
                        // STUDENT FIRESTORE DATA
                        // =====================================

                        val studentData =
                            hashMapOf(

                                "uid" to uid,

                                "fullName" to
                                        fullName,

                                "studentId" to
                                        studentId,

                                "course" to
                                        course,

                                "yearLevel" to
                                        yearLevel,

                                "email" to
                                        email,

                                "username" to
                                        username,

                                "interest" to
                                        interest,

                                "role" to
                                        "student",

                                "createdAt" to
                                        System.currentTimeMillis()
                            )


                        // =====================================
                        // SAVE USER
                        // =====================================

                        db.collection(
                            "users"
                        )
                            .document(
                                uid
                            )
                            .set(
                                studentData
                            )
                            .addOnSuccessListener {

                                resetSignUpButton(
                                    btnSignUp
                                )

                                Toast.makeText(
                                    this,
                                    "Account created successfully!",
                                    Toast.LENGTH_LONG
                                ).show()

                                finish()
                            }
                            .addOnFailureListener {
                                    error ->


                                resetSignUpButton(
                                    btnSignUp
                                )

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


                        resetSignUpButton(
                            btnSignUp
                        )


                        val message =
                            when {

                                task.exception
                                    ?.message
                                    ?.contains(
                                        "already in use",
                                        ignoreCase = true
                                    ) == true -> {

                                    "This student email is already registered."
                                }


                                task.exception
                                    ?.message
                                    ?.contains(
                                        "badly formatted",
                                        ignoreCase = true
                                    ) == true -> {

                                    "Invalid student email address."
                                }


                                task.exception
                                    ?.message
                                    ?.contains(
                                        "network",
                                        ignoreCase = true
                                    ) == true -> {

                                    "Network error. Please check your internet connection."
                                }


                                else -> {

                                    "Unable to create account. Please try again."
                                }
                            }


                        showError(
                            tvError,
                            message
                        )
                    }
                }
        }
    }


    // =========================================================
    // PASSWORD VISIBILITY
    // =========================================================

    private fun togglePasswordVisibility(
        editText: EditText,
        icon: ImageView,
        isVisible: Boolean
    ) {

        if (
            isVisible
        ) {

            editText.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            icon.setImageResource(
                R.drawable.ic_eye_off
            )

        } else {

            editText.inputType =
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD

            icon.setImageResource(
                R.drawable.ic_eye
            )
        }


        editText.setSelection(
            editText.text.length
        )
    }


    // =========================================================
    // SHOW ERROR
    // =========================================================

    private fun showError(
        tvError: TextView,
        message: String
    ) {

        tvError.text =
            message

        tvError.visibility =
            View.VISIBLE
    }


    // =========================================================
    // RESET BUTTON
    // =========================================================

    private fun resetSignUpButton(
        btnSignUp: LinearLayout
    ) {

        isRegistering =
            false

        btnSignUp.isEnabled =
            true

        btnSignUp.alpha =
            1.0f
    }
}