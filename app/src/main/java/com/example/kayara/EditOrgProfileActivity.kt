package com.example.kayara

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditOrgProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_org_profile)

        val etOrgName = findViewById<EditText>(R.id.etEditOrgName)
        val etOrgCategory = findViewById<EditText>(R.id.etEditOrgCategory)
        val etOrgDescription = findViewById<EditText>(R.id.etEditOrgDescription)
        val etOrgFounded = findViewById<EditText>(R.id.etEditOrgFounded)
        val etRecruitmentInfo = findViewById<EditText>(R.id.etEditRecruitmentInfo)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnSaveProfile = findViewById<LinearLayout>(R.id.btnSaveProfile)

        btnBack.setOnClickListener { finish() }

        btnSaveProfile.setOnClickListener {
            val orgName = etOrgName.text.toString().trim()
            val category = etOrgCategory.text.toString().trim()
            val description = etOrgDescription.text.toString().trim()
            val founded = etOrgFounded.text.toString().trim()
            val recruitmentInfo = etRecruitmentInfo.text.toString().trim()

            if (orgName.isEmpty()) {
                Toast.makeText(this, "Organization name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: I-update sa Firestore: organizations/{myOrgId}
            //   { orgName, category, description, founded, recruitmentInfo }

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

            val resultIntent = Intent()
            resultIntent.putExtra("orgName", orgName)
            resultIntent.putExtra("category", category)
            resultIntent.putExtra("description", description)
            resultIntent.putExtra("founded", founded)
            resultIntent.putExtra("recruitmentInfo", recruitmentInfo)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}