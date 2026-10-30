package com.example.kayara

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat

class StudentDashboardActivity :
    AppCompatActivity() {

    private lateinit var navHome:
            LinearLayout

    private lateinit var navOrgs:
            LinearLayout

    private lateinit var navNotifications:
            LinearLayout

    private lateinit var navProfile:
            LinearLayout

    private lateinit var iconHome:
            ImageView

    private lateinit var iconOrgs:
            ImageView

    private lateinit var iconNotifications:
            ImageView

    private lateinit var iconProfile:
            ImageView

    private lateinit var labelHome:
            TextView

    private lateinit var labelOrgs:
            TextView

    private lateinit var labelNotifications:
            TextView

    private lateinit var labelProfile:
            TextView

    private lateinit var tvUserInitials:
            TextView

    private lateinit var tvGreeting:
            TextView

    private lateinit var tvSubGreeting:
            TextView

    private lateinit var auth:
            FirebaseAuth

    private lateinit var db:
            FirebaseFirestore

    private var userListener:
            ListenerRegistration? =
        null

    private var currentTab =
        ""

    override fun onCreate(
        savedInstanceState:
        Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        // =====================================================
        // FIX PURPLE STATUS BAR
        // =====================================================

        window.statusBarColor =
            getColor(
                R.color.teal_deep_text
            )

        window.navigationBarColor =
            getColor(
                R.color.bg_light
            )

        WindowCompat
            .getInsetsController(
                window,
                window.decorView
            ).apply {

                isAppearanceLightStatusBars =
                    false

                isAppearanceLightNavigationBars =
                    true
            }

        setupSystemBars(
            getColor(R.color.teal_deep_text),
            getColor(R.color.bg_light)
        )

        setContentView(
            R.layout.activity_student_dashboard
        )

        setContentView(
            R.layout.activity_student_dashboard
        )

        // =====================================================
        // FIREBASE
        // =====================================================

        auth =
            FirebaseAuth
                .getInstance()

        db =
            FirebaseFirestore
                .getInstance()

        // =====================================================
        // HEADER
        // =====================================================

        tvUserInitials =
            findViewById(
                R.id.tvUserInitials
            )

        tvGreeting =
            findViewById(
                R.id.tvGreeting
            )

        tvSubGreeting =
            findViewById(
                R.id.tvSubGreeting
            )

        // =====================================================
        // NAV
        // =====================================================

        navHome =
            findViewById(
                R.id.navHome
            )

        navOrgs =
            findViewById(
                R.id.navOrgs
            )

        navNotifications =
            findViewById(
                R.id.navNotifications
            )

        navProfile =
            findViewById(
                R.id.navProfile
            )

        iconHome =
            findViewById(
                R.id.iconHome
            )

        iconOrgs =
            findViewById(
                R.id.iconOrgs
            )

        iconNotifications =
            findViewById(
                R.id.iconNotifications
            )

        iconProfile =
            findViewById(
                R.id.iconProfile
            )

        labelHome =
            findViewById(
                R.id.labelHome
            )

        labelOrgs =
            findViewById(
                R.id.labelOrgs
            )

        labelNotifications =
            findViewById(
                R.id.labelNotifications
            )

        labelProfile =
            findViewById(
                R.id.labelProfile
            )

        // =====================================================
        // DATA
        // =====================================================

        loadStudentInformation()

        // =====================================================
        // CHAT
        // =====================================================

        findViewById<FrameLayout>(
            R.id.btnChat
        ).setOnClickListener {

            openStudentChat(
                this
            )
        }

        // =====================================================
        // INITIAL TAB
        // =====================================================

        if (
            savedInstanceState ==
            null
        ) {

            openTab(
                "home"
            )

        } else {

            restoreActiveTab()
        }

        // =====================================================
        // NAV CLICKS
        // =====================================================

        navHome.setOnClickListener {

            openTab(
                "home"
            )
        }

        navOrgs.setOnClickListener {

            openTab(
                "orgs"
            )
        }

        navNotifications
            .setOnClickListener {

                openTab(
                    "notifications"
                )
            }

        navProfile.setOnClickListener {

            openTab(
                "profile"
            )
        }
    }

    // =========================================================
    // STUDENT DATA
    // =========================================================

    private fun loadStudentInformation() {

        val currentUser =
            auth.currentUser

        if (
            currentUser ==
            null
        ) {

            Toast.makeText(
                this,
                "Please login again.",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        userListener
            ?.remove()

        userListener =
            db.collection(
                "users"
            )
                .document(
                    currentUser.uid
                )
                .addSnapshotListener {
                        document,
                        error ->

                    if (
                        error !=
                        null
                    ) {

                        tvGreeting.text =
                            "Hi, Student!"

                        tvSubGreeting.text =
                            "Welcome to BBCO"

                        tvUserInitials.text =
                            "ST"

                        return@addSnapshotListener
                    }

                    if (
                        document != null &&
                        document.exists()
                    ) {

                        val fullName =
                            document
                                .getString(
                                    "fullName"
                                )
                                ?: "Student"

                        val course =
                            document
                                .getString(
                                    "course"
                                )
                                ?: ""

                        val yearLevel =
                            document
                                .getString(
                                    "yearLevel"
                                )
                                ?: ""

                        tvGreeting.text =
                            "Hi, ${
                                getFirstName(
                                    fullName
                                )
                            }!"

                        tvSubGreeting.text =
                            if (
                                course
                                    .isNotEmpty() &&
                                yearLevel
                                    .isNotEmpty()
                            ) {

                                "$course · $yearLevel"

                            } else {

                                "Welcome to BBCO"
                            }

                        tvUserInitials.text =
                            getInitials(
                                fullName
                            )

                    } else {

                        tvGreeting.text =
                            "Hi, Student!"

                        tvSubGreeting.text =
                            "Welcome to BBCO"

                        tvUserInitials.text =
                            "ST"
                    }
                }
    }

    private fun getFirstName(
        fullName:
        String
    ): String {

        val parts =
            fullName
                .trim()
                .split(
                    "\\s+".toRegex()
                )
                .filter {
                    it.isNotEmpty()
                }

        return if (
            parts.isNotEmpty()
        ) {

            parts[0]

        } else {

            "Student"
        }
    }

    private fun getInitials(
        fullName:
        String
    ): String {

        val parts =
            fullName
                .trim()
                .split(
                    "\\s+".toRegex()
                )
                .filter {
                    it.isNotEmpty()
                }

        return when {

            parts.size >= 2 ->

                "${parts[0].first()}${parts[1].first()}"

            parts.size == 1 ->

                parts[0]
                    .take(2)

            else ->

                "ST"

        }.uppercase()
    }

    private fun setupSystemBars(
        statusBarColor: Int,
        navigationBarColor: Int
    ) {

        WindowCompat.setDecorFitsSystemWindows(
            window,
            true
        )

        window.clearFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )

        window.addFlags(
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        )

        window.statusBarColor =
            statusBarColor

        window.navigationBarColor =
            navigationBarColor

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).apply {

            isAppearanceLightStatusBars =
                false

            isAppearanceLightNavigationBars =
                true
        }
    }

    // =========================================================
    // OPEN ORGS FROM HOME
    // =========================================================

    fun navigateToOrgsTab() {

        openTab(
            "orgs"
        )
    }

    // =========================================================
    // TAB
    // =========================================================

    private fun openTab(
        tab:
        String
    ) {

        if (
            tab ==
            currentTab
        ) {
            return
        }

        val fragment:
                Fragment =
            when (
                tab
            ) {

                "orgs" ->

                    BrowseOrgsFragment()

                "notifications" ->

                    StudentNotificationsFragment()

                "profile" ->

                    ProfileFragment()

                else ->

                    StudentDashboardFragment()
            }

        loadFragment(
            fragment
        )

        setActiveTab(
            tab
        )
    }

    private fun restoreActiveTab() {

        val fragment =
            supportFragmentManager
                .findFragmentById(
                    R.id.fragmentContainer
                )

        val tab =
            when (
                fragment
            ) {

                is BrowseOrgsFragment ->

                    "orgs"

                is StudentNotificationsFragment ->

                    "notifications"

                is ProfileFragment ->

                    "profile"

                else ->

                    "home"
            }

        setActiveTab(
            tab
        )
    }

    private fun loadFragment(
        fragment:
        Fragment
    ) {

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .commit()
    }

    // =========================================================
    // ACTIVE TAB
    // =========================================================

    private fun setActiveTab(
        tab:
        String
    ) {

        currentTab =
            tab

        val greetingSection =
            findViewById<View>(
                R.id.headerGreetingSection
            )

        greetingSection.visibility =
            if (
                tab ==
                "home"
            ) {

                View.VISIBLE

            } else {

                View.GONE
            }

        val selectedPosition =
            when (
                tab
            ) {

                "orgs" ->
                    1

                "notifications" ->
                    2

                "profile" ->
                    3

                else ->
                    0
            }

        updateBottomNav(
            selectedPosition
        )
    }

    // =========================================================
    // SMOOTH NAV
    // =========================================================

    private fun updateBottomNav(
        selectedPosition:
        Int
    ) {

        val icons =
            listOf(
                iconHome,
                iconOrgs,
                iconNotifications,
                iconProfile
            )

        val labels =
            listOf(
                labelHome,
                labelOrgs,
                labelNotifications,
                labelProfile
            )

        val activeColor =
            getColor(
                R.color.teal_deep_text
            )

        val inactiveColor =
            Color.parseColor(
                "#AAB7C8"
            )

        val density =
            resources
                .displayMetrics
                .density

        icons.forEachIndexed {
                index,
                icon ->

            val selected =
                index ==
                        selectedPosition

            icon.animate()
                .cancel()

            labels[index]
                .animate()
                .cancel()

            icon.setColorFilter(
                if (
                    selected
                )
                    activeColor
                else
                    inactiveColor
            )

            labels[index]
                .setTextColor(
                    if (
                        selected
                    )
                        activeColor
                    else
                        inactiveColor
                )

            if (
                selected
            ) {

                icon.background =
                    GradientDrawable()
                        .apply {

                            shape =
                                GradientDrawable.OVAL

                            setColor(
                                Color.WHITE
                            )

                            setStroke(
                                (1f * density)
                                    .toInt(),

                                Color.parseColor(
                                    "#D9EFEB"
                                )
                            )
                        }

                icon.elevation =
                    10f *
                            density

                icon.alpha =
                    0.82f

                icon.scaleX =
                    0.92f

                icon.scaleY =
                    0.92f

                icon.animate()
                    .translationY(
                        -18f *
                                density
                    )
                    .scaleX(1.15f)
                    .scaleY(1.15f)
                    .alpha(1f)
                    .setDuration(300L)
                    .setInterpolator(
                        AccelerateDecelerateInterpolator()
                    )
                    .start()

                labels[index]
                    .animate()
                    .translationY(
                        -6f *
                                density
                    )
                    .alpha(1f)
                    .setDuration(300L)
                    .start()

            } else {

                icon.animate()
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(0.68f)
                    .setDuration(250L)
                    .setInterpolator(
                        AccelerateDecelerateInterpolator()
                    )
                    .withEndAction {

                        if (
                            index !=
                            selectedPosition
                        ) {

                            icon.background =
                                ColorDrawable(
                                    Color.TRANSPARENT
                                )

                            icon.elevation =
                                0f
                        }
                    }
                    .start()

                labels[index]
                    .animate()
                    .translationY(0f)
                    .alpha(0.72f)
                    .setDuration(250L)
                    .start()
            }
        }
    }

    override fun onDestroy() {

        userListener
            ?.remove()

        userListener =
            null

        super.onDestroy()
    }
}