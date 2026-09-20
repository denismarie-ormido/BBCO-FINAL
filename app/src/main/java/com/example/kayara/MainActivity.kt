package com.example.kayara

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<LinearLayout>(R.id.btnSuperAdmin).setOnClickListener {
            startActivity(Intent(this, SuperAdminLoginActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnAdmin).setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnStudent).setOnClickListener {
            startActivity(Intent(this, StudentLoginActivity::class.java))
        }
    }
}