package com.example.kayara

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class SuperAdminDashboardActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: FrameLayout
    private lateinit var fab: ImageView

    private lateinit var navHome: View
    private lateinit var navOrganizations: View
    private lateinit var navNotifications: View
    private lateinit var navDocuments: View

    private lateinit var iconHome: ImageView
    private lateinit var iconOrganizations: ImageView
    private lateinit var iconNotifications: ImageView
    private lateinit var iconDocuments: ImageView

    private lateinit var underlineHome: View
    private lateinit var underlineOrganizations: View
    private lateinit var underlineNotifications: View
    private lateinit var underlineDocuments: View

    private val activeColor = android.graphics.Color.parseColor("#173F7A")
    private val inactiveColor = android.graphics.Color.parseColor("#AAB7C8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_super_admin_dashboard)

        // ---------------------------------------------------------
        // MAIN VIEWS
        // ---------------------------------------------------------

        fragmentContainer = findViewById(R.id.fragmentContainer)
        fab = findViewById(R.id.fab)

        // ---------------------------------------------------------
        // BOTTOM NAVIGATION
        // ---------------------------------------------------------

        navHome = findViewById(R.id.navHome)
        navOrganizations = findViewById(R.id.navOrganizations)
        navNotifications = findViewById(R.id.navNotifications)
        navDocuments = findViewById(R.id.navDocuments)

        iconHome = findViewById(R.id.iconHome)
        iconOrganizations = findViewById(R.id.iconOrganizations)
        iconNotifications = findViewById(R.id.iconNotifications)
        iconDocuments = findViewById(R.id.iconDocuments)

        underlineHome = findViewById(R.id.underlineHome)
        underlineOrganizations = findViewById(R.id.underlineOrganizations)
        underlineNotifications = findViewById(R.id.underlineNotifications)
        underlineDocuments = findViewById(R.id.underlineDocuments)

        // ---------------------------------------------------------
        // PROFILE
        // ---------------------------------------------------------

        findViewById<FrameLayout>(R.id.btnProfile).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SuperAdminProfileActivity::class.java
                )
            )
        }

        // ---------------------------------------------------------
        // DEFAULT TAB
        // ---------------------------------------------------------

        if (savedInstanceState == null) {
            showHome()
        }

        // ---------------------------------------------------------
        // NAVIGATION
        // ---------------------------------------------------------

        navHome.setOnClickListener {
            showHome()
        }

        navOrganizations.setOnClickListener {
            showOrganizations()
        }

        navNotifications.setOnClickListener {
            showNotifications()
        }

        navDocuments.setOnClickListener {
            showDocuments()
        }
    }

    // =============================================================
    // HOME
    // =============================================================

    private fun showHome() {

        loadFragment(DashboardFragment())

        fab.visibility = View.GONE

        setActiveTab(
            activeIcon = iconHome,
            activeUnderline = underlineHome
        )
    }

    // =============================================================
    // ORGANIZATIONS
    // =============================================================

    private fun showOrganizations() {

        loadFragment(OrganizationsFragment())

        fab.visibility = View.VISIBLE

        setActiveTab(
            activeIcon = iconOrganizations,
            activeUnderline = underlineOrganizations
        )

        fab.setOnClickListener {
            // Existing OrganizationsFragment/FAB functionality
            // can remain connected here.
        }
    }

    // =============================================================
    // NOTIFICATIONS
    // =============================================================

    private fun showNotifications() {

        loadFragment(NotificationsFragment())

        fab.visibility = View.GONE

        setActiveTab(
            activeIcon = iconNotifications,
            activeUnderline = underlineNotifications
        )
    }

    // =============================================================
    // DOCUMENTS
    // =============================================================

    private fun showDocuments() {

        loadFragment(DocumentsFragment())

        fab.visibility = View.GONE

        setActiveTab(
            activeIcon = iconDocuments,
            activeUnderline = underlineDocuments
        )
    }

    // =============================================================
    // CHANGE SELECTED NAVIGATION ITEM
    // =============================================================

    private fun setActiveTab(
        activeIcon: ImageView,
        activeUnderline: View
    ) {

        // All icons inactive
        iconHome.setColorFilter(inactiveColor)
        iconOrganizations.setColorFilter(inactiveColor)
        iconNotifications.setColorFilter(inactiveColor)
        iconDocuments.setColorFilter(inactiveColor)

        // All underlines invisible
        underlineHome.visibility = View.INVISIBLE
        underlineOrganizations.visibility = View.INVISIBLE
        underlineNotifications.visibility = View.INVISIBLE
        underlineDocuments.visibility = View.INVISIBLE

        // Selected item
        activeIcon.setColorFilter(activeColor)
        activeUnderline.visibility = View.VISIBLE
    }

    // =============================================================
    // FRAGMENT LOADER
    // =============================================================

    private fun loadFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}