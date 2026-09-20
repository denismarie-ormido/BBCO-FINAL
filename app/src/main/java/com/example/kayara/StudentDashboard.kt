package com.example.kayara

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navOrgs: LinearLayout
    private lateinit var navNotifications: LinearLayout
    private lateinit var navProfile: LinearLayout

    private lateinit var iconHome: ImageView
    private lateinit var iconOrgs: ImageView
    private lateinit var iconNotifications: ImageView
    private lateinit var iconProfile: ImageView

    private lateinit var underlineHome: View
    private lateinit var underlineOrgs: View
    private lateinit var underlineNotifications: View
    private lateinit var underlineProfile: View

    private lateinit var tvUserInitials: TextView
    private lateinit var tvGreeting: TextView
    private lateinit var tvSubGreeting: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Header
        tvUserInitials = findViewById(R.id.tvUserInitials)
        tvGreeting = findViewById(R.id.tvGreeting)
        tvSubGreeting = findViewById(R.id.tvSubGreeting)

        // Bottom navigation
        navHome = findViewById(R.id.navHome)
        navOrgs = findViewById(R.id.navOrgs)
        navNotifications = findViewById(R.id.navNotifications)
        navProfile = findViewById(R.id.navProfile)

        iconHome = findViewById(R.id.iconHome)
        iconOrgs = findViewById(R.id.iconOrgs)
        iconNotifications = findViewById(R.id.iconNotifications)
        iconProfile = findViewById(R.id.iconProfile)

        underlineHome = findViewById(R.id.underlineHome)
        underlineOrgs = findViewById(R.id.underlineOrgs)
        underlineNotifications = findViewById(R.id.underlineNotifications)
        underlineProfile = findViewById(R.id.underlineProfile)

        // Load logged-in student's information
        loadStudentInformation()

        // Default tab
        if (savedInstanceState == null) {
            loadFragment(StudentDashboardFragment())
            setActiveTab("home")
        }

        // Home
        navHome.setOnClickListener {
            loadFragment(StudentDashboardFragment())
            setActiveTab("home")
        }

        // Organizations
        navOrgs.setOnClickListener {
            loadFragment(BrowseOrgsFragment())
            setActiveTab("orgs")
        }

        // Notifications
        navNotifications.setOnClickListener {
            loadFragment(StudentNotificationsFragment())
            setActiveTab("notifications")
        }

        // Profile
        navProfile.setOnClickListener {
            loadFragment(ProfileFragment())
            setActiveTab("profile")
        }
    }

    private fun loadStudentInformation() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(
                this,
                "Please login again.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val uid = currentUser.uid

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val fullName =
                        document.getString("fullName") ?: "Student"

                    val course =
                        document.getString("course") ?: ""

                    val yearLevel =
                        document.getString("yearLevel") ?: ""

                    // Greeting
                    tvGreeting.text = "Hi, ${getFirstName(fullName)}!"

                    // Course and year
                    tvSubGreeting.text =
                        if (course.isNotEmpty() && yearLevel.isNotEmpty()) {
                            "$course · $yearLevel"
                        } else {
                            "Welcome to BBCO"
                        }

                    // Initials
                    tvUserInitials.text =
                        getInitials(fullName)

                } else {

                    tvGreeting.text = "Hi, Student!"
                    tvSubGreeting.text = "Welcome to BBCO"
                    tvUserInitials.text = "ST"
                }
            }
            .addOnFailureListener {

                tvGreeting.text = "Hi, Student!"
                tvSubGreeting.text = "Welcome to BBCO"
                tvUserInitials.text = "ST"

                Toast.makeText(
                    this,
                    "Unable to load student information.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getFirstName(fullName: String): String {

        val parts = fullName.trim().split(" ")

        return if (parts.isNotEmpty()) {
            parts[0]
        } else {
            "Student"
        }
    }

    private fun getInitials(fullName: String): String {

        val parts = fullName.trim()
            .split("\\s+".toRegex())
            .filter { it.isNotEmpty() }

        return when {
            parts.size >= 2 -> {
                "${parts[0].first()}${parts[1].first()}"
            }

            parts.size == 1 -> {
                parts[0].take(2).uppercase()
            }

            else -> {
                "ST"
            }
        }.uppercase()
    }

    fun navigateToOrgsTab() {
        loadFragment(BrowseOrgsFragment())
        setActiveTab("orgs")
    }

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .commit()
    }

    private fun setActiveTab(tab: String) {

        val activeColor =
            getColor(R.color.navy)

        val inactiveColor =
            getColor(R.color.placeholder_gray)

        iconHome.setColorFilter(
            if (tab == "home")
                activeColor
            else
                inactiveColor
        )

        iconOrgs.setColorFilter(
            if (tab == "orgs")
                activeColor
            else
                inactiveColor
        )

        iconNotifications.setColorFilter(
            if (tab == "notifications")
                activeColor
            else
                inactiveColor
        )

        iconProfile.setColorFilter(
            if (tab == "profile")
                activeColor
            else
                inactiveColor
        )

        underlineHome.visibility =
            if (tab == "home")
                View.VISIBLE
            else
                View.INVISIBLE

        underlineOrgs.visibility =
            if (tab == "orgs")
                View.VISIBLE
            else
                View.INVISIBLE

        underlineNotifications.visibility =
            if (tab == "notifications")
                View.VISIBLE
            else
                View.INVISIBLE

        underlineProfile.visibility =
            if (tab == "profile")
                View.VISIBLE
            else
                View.INVISIBLE
    }
}