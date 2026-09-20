package com.example.kayara

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navMembers: LinearLayout
    private lateinit var navNotifications: LinearLayout
    private lateinit var navRequirements: LinearLayout
    private lateinit var navProfile: LinearLayout

    private lateinit var iconHome: ImageView
    private lateinit var iconMembers: ImageView
    private lateinit var iconNotifications: ImageView
    private lateinit var iconRequirements: ImageView
    private lateinit var iconProfile: ImageView

    private lateinit var underlineHome: View
    private lateinit var underlineMembers: View
    private lateinit var underlineNotifications: View
    private lateinit var underlineRequirements: View
    private lateinit var underlineProfile: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Bottom nav views
        navHome = findViewById(R.id.navHome)
        navMembers = findViewById(R.id.navMembers)
        navNotifications = findViewById(R.id.navNotifications)
        navRequirements = findViewById(R.id.navRequirements)
        navProfile = findViewById(R.id.navProfile)

        iconHome = findViewById(R.id.iconHome)
        iconMembers = findViewById(R.id.iconMembers)
        iconNotifications = findViewById(R.id.iconNotifications)
        iconRequirements = findViewById(R.id.iconRequirements)
        iconProfile = findViewById(R.id.iconProfile)

        underlineHome = findViewById(R.id.underlineHome)
        underlineMembers = findViewById(R.id.underlineMembers)
        underlineNotifications = findViewById(R.id.underlineNotifications)
        underlineRequirements = findViewById(R.id.underlineRequirements)
        underlineProfile = findViewById(R.id.underlineProfile)

        // Header profile button
        findViewById<FrameLayout>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, AdminProfileActivity::class.java))
        }

        if (savedInstanceState == null) {
            loadFragment(AdminDashboardFragment())
            setActiveTab("home")
        }

        navHome.setOnClickListener {
            loadFragment(AdminDashboardFragment())
            setActiveTab("home")
        }
        navMembers.setOnClickListener {
            loadFragment(MembersFragment())
            setActiveTab("members")
        }
        navNotifications.setOnClickListener {
            loadFragment(AdminNotificationsFragment())
            setActiveTab("notifications")
        }
        navRequirements.setOnClickListener {
            loadFragment(RequirementsFragment())
            setActiveTab("requirements")
        }
        navProfile.setOnClickListener {
            loadFragment(OrganizationProfileFragment())
            setActiveTab("profile")
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun setActiveTab(tab: String) {
        val activeColor = getColor(R.color.navy)
        val inactiveColor = getColor(R.color.placeholder_gray)

        iconHome.setColorFilter(if (tab == "home") activeColor else inactiveColor)
        iconMembers.setColorFilter(if (tab == "members") activeColor else inactiveColor)
        iconNotifications.setColorFilter(if (tab == "notifications") activeColor else inactiveColor)
        iconRequirements.setColorFilter(if (tab == "requirements") activeColor else inactiveColor)
        iconProfile.setColorFilter(if (tab == "profile") activeColor else inactiveColor)

        underlineHome.visibility = if (tab == "home") View.VISIBLE else View.INVISIBLE
        underlineMembers.visibility = if (tab == "members") View.VISIBLE else View.INVISIBLE
        underlineNotifications.visibility = if (tab == "notifications") View.VISIBLE else View.INVISIBLE
        underlineRequirements.visibility = if (tab == "requirements") View.VISIBLE else View.INVISIBLE
        underlineProfile.visibility = if (tab == "profile") View.VISIBLE else View.INVISIBLE
    }
}