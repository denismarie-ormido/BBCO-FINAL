package com.example.kayara

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditStudentProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student_profile)

        val etFullName = findViewById<EditText>(R.id.etEditFullName)
        val etContact = findViewById<EditText>(R.id.etEditContact)
        val spinnerYearLevel = findViewById<Spinner>(R.id.spinnerEditYearLevel)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnSave = findViewById<LinearLayout>(R.id.btnSaveStudentProfile)

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val contact = etContact.text.toString().trim()
            val yearLevel = spinnerYearLevel.selectedItem.toString()

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Full name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: I-update sa Firestore: users/{myUserId}
            //   { fullName, contact, yearLevel }

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}