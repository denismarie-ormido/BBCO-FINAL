package com.example.kayara

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale


class ProfileFragment : Fragment() {

    // =========================================================
    // FIREBASE
    // =========================================================

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage


    // =========================================================
    // PROFILE VIEWS
    // =========================================================

    private lateinit var ivProfile: ShapeableImageView

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileCourse: TextView
    private lateinit var tvProfileStudentId: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfileContact: TextView
    private lateinit var tvProfileYearLevel: TextView


    // =========================================================
    // MEMBERSHIP CONTAINER
    // =========================================================

    private lateinit var membershipContainer: LinearLayout


    // =========================================================
    // SELECTED IMAGE
    // =========================================================

    private var selectedImageUri: Uri? = null


    // =========================================================
    // IMAGE PICKER
    // =========================================================

    private val pickImage =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedImageUri = uri

                // Show selected image immediately
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.bg_profile_avatar)
                    .into(ivProfile)

                // Upload to Firebase Storage
                uploadProfilePicture(uri)
            }
        }


    // =========================================================
    // ON CREATE VIEW
    // =========================================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_profile,
            container,
            false
        )


        // =====================================================
        // INITIALIZE FIREBASE
        // =====================================================

        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()

        storage = FirebaseStorage.getInstance()


        // =====================================================
        // INITIALIZE PROFILE VIEWS
        // =====================================================

        ivProfile =
            view.findViewById(R.id.ivProfile)

        tvProfileName =
            view.findViewById(R.id.tvProfileName)

        tvProfileCourse =
            view.findViewById(R.id.tvProfileCourse)

        tvProfileStudentId =
            view.findViewById(R.id.tvProfileStudentId)

        tvProfileEmail =
            view.findViewById(R.id.tvProfileEmail)

        tvProfileContact =
            view.findViewById(R.id.tvProfileContact)

        tvProfileYearLevel =
            view.findViewById(R.id.tvProfileYearLevel)


        // =====================================================
        // MEMBERSHIP CONTAINER
        // =====================================================

        membershipContainer =
            view.findViewById(R.id.membershipContainer)


        // =====================================================
        // MAKE PROFILE IMAGE CIRCULAR
        // =====================================================

        ivProfile.shapeAppearanceModel =
            ivProfile.shapeAppearanceModel
                .toBuilder()
                .setAllCornerSizes(1000f)
                .build()


        // =====================================================
        // LOAD PROFILE
        // =====================================================

        loadStudentProfile()


        // =====================================================
        // LOAD APPROVED MEMBERSHIPS
        // =====================================================

        loadStudentMemberships()


        // =====================================================
        // CHANGE PROFILE PHOTO
        // =====================================================

        view.findViewById<LinearLayout>(
            R.id.btnChangePhoto
        ).setOnClickListener {

            pickImage.launch("image/*")
        }


        // =====================================================
        // EDIT PROFILE
        // =====================================================

        view.findViewById<LinearLayout>(
            R.id.btnEditProfile
        ).setOnClickListener {

            showEditProfileDialog()
        }


        // =====================================================
        // LOGOUT
        // =====================================================

        view.findViewById<LinearLayout>(
            R.id.btnLogout
        ).setOnClickListener {

            auth.signOut()

            val intent = Intent(
                requireContext(),
                WelcomeActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }


        return view
    }


    // =========================================================
    // LOAD STUDENT PROFILE
    // =========================================================

    private fun loadStudentProfile() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            return
        }

        val uid = currentUser.uid


        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    return@addOnSuccessListener
                }


                // Full name
                val fullName =
                    document.getString("fullName")
                        ?: "Student"


                // Student ID
                val studentId =
                    document.getString("studentId")
                        ?: "Not set"


                // Course
                val course =
                    document.getString("course")
                        ?: "Not set"


                // Year level
                val yearLevel =
                    document.getString("yearLevel")
                        ?: "Not set"


                // Email
                val email =
                    document.getString("email")
                        ?: currentUser.email
                        ?: "Not set"


                // Contact
                val contact =
                    document.getString("contactNumber")
                        ?: "Not set"


                // Profile image
                val profileImageUrl =
                    document.getString("profileImageUrl")


                // =================================================
                // DISPLAY PROFILE INFORMATION
                // =================================================

                tvProfileName.text =
                    fullName


                tvProfileCourse.text =
                    "$course · BISU Balilihan Campus"


                tvProfileStudentId.text =
                    "Student ID: $studentId"


                tvProfileEmail.text =
                    email


                tvProfileContact.text =
                    contact


                tvProfileYearLevel.text =
                    yearLevel


                // =================================================
                // LOAD PROFILE IMAGE
                // =================================================

                if (!profileImageUrl.isNullOrEmpty()) {

                    Glide.with(this)
                        .load(profileImageUrl)
                        .placeholder(
                            R.drawable.bg_profile_avatar
                        )
                        .error(
                            R.drawable.bg_profile_avatar
                        )
                        .into(ivProfile)
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Unable to load profile information.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    // =========================================================
    // LOAD STUDENT MEMBERSHIPS
    // =========================================================

    private fun loadStudentMemberships() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            return
        }

        val uid = currentUser.uid


        // Clear old membership cards
        membershipContainer.removeAllViews()


        db.collection("memberships")
            .whereEqualTo("studentId", uid)
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener { result ->

                // Clear again before displaying
                membershipContainer.removeAllViews()


                // =================================================
                // NO APPROVED MEMBERSHIPS
                // =================================================

                if (result.isEmpty) {

                    val emptyText =
                        TextView(requireContext())


                    emptyText.text =
                        "You are not yet a member of any organization."


                    emptyText.setTextColor(
                        resources.getColor(
                            R.color.text_gray,
                            null
                        )
                    )


                    emptyText.textSize =
                        12f


                    emptyText.setPadding(
                        12.dp(),
                        12.dp(),
                        12.dp(),
                        18.dp()
                    )


                    membershipContainer.addView(
                        emptyText
                    )


                    return@addOnSuccessListener
                }


                // =================================================
                // DISPLAY APPROVED MEMBERSHIPS
                // =================================================

                for (document in result) {

                    val organizationName =
                        document.getString(
                            "organizationName"
                        ) ?: "Organization"


                    val shortCode =
                        document.getString(
                            "shortCode"
                        ) ?: "ORG"


                    val dateApplied =
                        document.getTimestamp(
                            "dateApplied"
                        )


                    addMembershipCard(
                        organizationName,
                        shortCode,
                        dateApplied
                    )
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Unable to load memberships.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    // =========================================================
    // CREATE MEMBERSHIP CARD
    // =========================================================

    private fun addMembershipCard(
        organizationName: String,
        shortCode: String,
        dateApplied: Timestamp?
    ) {

        // =====================================================
        // CARD
        // =====================================================

        val card =
            LinearLayout(requireContext())


        card.orientation =
            LinearLayout.HORIZONTAL


        card.gravity =
            Gravity.CENTER_VERTICAL


        card.setBackgroundResource(
            R.drawable.bg_card
        )


        card.setPadding(
            12.dp(),
            12.dp(),
            12.dp(),
            12.dp()
        )


        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )


        cardParams.setMargins(
            0,
            0,
            0,
            9.dp()
        )


        card.layoutParams =
            cardParams


        // =====================================================
        // ORGANIZATION AVATAR
        // =====================================================

        val avatar =
            TextView(requireContext())


        val avatarParams =
            LinearLayout.LayoutParams(
                36.dp(),
                36.dp()
            )


        avatar.layoutParams =
            avatarParams


        avatar.gravity =
            Gravity.CENTER


        avatar.text =
            shortCode
                .take(4)
                .uppercase()


        avatar.setTextColor(
            resources.getColor(
                R.color.white,
                null
            )
        )


        avatar.textSize =
            9f


        avatar.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )


        avatar.setBackgroundResource(
            R.drawable.bg_oc_avatar_teal
        )


        // =====================================================
        // ORGANIZATION INFORMATION CONTAINER
        // =====================================================

        val info =
            LinearLayout(requireContext())


        info.orientation =
            LinearLayout.VERTICAL


        val infoParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )


        infoParams.setMargins(
            11.dp(),
            0,
            0,
            0
        )


        info.layoutParams =
            infoParams


        // =====================================================
        // ORGANIZATION NAME
        // =====================================================

        val name =
            TextView(requireContext())


        name.text =
            organizationName


        name.setTextColor(
            resources.getColor(
                R.color.dark_blue,
                null
            )
        )


        name.textSize =
            12.5f


        name.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )


        // =====================================================
        // MEMBERSHIP DATE
        // =====================================================

        val details =
            TextView(requireContext())


        val dateText: String


        if (dateApplied != null) {

            val dateFormat =
                SimpleDateFormat(
                    "MMMM yyyy",
                    Locale.getDefault()
                )


            val formattedDate =
                dateFormat.format(
                    dateApplied.toDate()
                )


            dateText =
                "Member since $formattedDate"

        } else {

            dateText =
                "Active member"
        }


        details.text =
            dateText


        details.setTextColor(
            resources.getColor(
                R.color.text_gray,
                null
            )
        )


        details.textSize =
            10.5f


        // =====================================================
        // ADD TEXTS TO INFO
        // =====================================================

        info.addView(name)

        info.addView(details)


        // =====================================================
        // ADD EVERYTHING TO CARD
        // =====================================================

        card.addView(avatar)

        card.addView(info)


        // =====================================================
        // ADD CARD TO MEMBERSHIP CONTAINER
        // =====================================================

        membershipContainer.addView(card)
    }


    // =========================================================
    // EDIT PROFILE
    // =========================================================

    private fun showEditProfileDialog() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            return
        }

        val uid = currentUser.uid


        // Get current values from Firestore
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    return@addOnSuccessListener
                }


                val fullName =
                    document.getString("fullName")
                        ?: ""


                val studentId =
                    document.getString("studentId")
                        ?: ""


                val course =
                    document.getString("course")
                        ?: ""


                val yearLevel =
                    document.getString("yearLevel")
                        ?: ""


                val interest =
                    document.getString("interest")
                        ?: ""


                val contact =
                    document.getString("contactNumber")
                        ?: ""


                // =================================================
                // DIALOG LAYOUT
                // =================================================

                val layout =
                    LinearLayout(requireContext())


                layout.orientation =
                    LinearLayout.VERTICAL


                layout.setPadding(
                    45,
                    10,
                    45,
                    10
                )


                // =================================================
                // FULL NAME
                // =================================================

                val etFullName =
                    EditText(requireContext())


                etFullName.hint =
                    "Full Name"


                etFullName.setText(
                    fullName
                )


                // =================================================
                // STUDENT ID
                // =================================================

                val etStudentId =
                    EditText(requireContext())


                etStudentId.hint =
                    "Student ID"


                etStudentId.setText(
                    studentId
                )


                // =================================================
                // COURSE
                // =================================================

                val etCourse =
                    EditText(requireContext())


                etCourse.hint =
                    "Course"


                etCourse.setText(
                    course
                )


                // =================================================
                // YEAR LEVEL
                // =================================================

                val etYearLevel =
                    EditText(requireContext())


                etYearLevel.hint =
                    "Year Level"


                etYearLevel.setText(
                    yearLevel
                )


                // =================================================
                // INTEREST
                // =================================================

                val etInterest =
                    EditText(requireContext())


                etInterest.hint =
                    "Interest"


                etInterest.setText(
                    interest
                )


                // =================================================
                // CONTACT NUMBER
                // =================================================

                val etContact =
                    EditText(requireContext())


                etContact.hint =
                    "Contact Number"


                etContact.setText(
                    contact
                )


                etContact.inputType =
                    InputType.TYPE_CLASS_PHONE


                // =================================================
                // ADD FIELDS
                // =================================================

                layout.addView(
                    etFullName
                )

                layout.addView(
                    etStudentId
                )

                layout.addView(
                    etCourse
                )

                layout.addView(
                    etYearLevel
                )

                layout.addView(
                    etInterest
                )

                layout.addView(
                    etContact
                )


                // =================================================
                // CREATE DIALOG
                // =================================================

                val dialog =
                    AlertDialog.Builder(
                        requireContext()
                    )
                        .setTitle("Edit Profile")
                        .setView(layout)
                        .setNegativeButton(
                            "Cancel",
                            null
                        )
                        .setPositiveButton(
                            "Save",
                            null
                        )
                        .create()


                // =================================================
                // SAVE BUTTON
                // =================================================

                dialog.setOnShowListener {

                    dialog.getButton(
                        AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener {

                        val updatedName =
                            etFullName.text
                                .toString()
                                .trim()


                        val updatedStudentId =
                            etStudentId.text
                                .toString()
                                .trim()


                        val updatedCourse =
                            etCourse.text
                                .toString()
                                .trim()


                        val updatedYearLevel =
                            etYearLevel.text
                                .toString()
                                .trim()


                        val updatedInterest =
                            etInterest.text
                                .toString()
                                .trim()


                        val updatedContact =
                            etContact.text
                                .toString()
                                .trim()


                        // =================================================
                        // VALIDATION
                        // =================================================

                        if (
                            updatedName.isEmpty() ||
                            updatedStudentId.isEmpty() ||
                            updatedCourse.isEmpty() ||
                            updatedYearLevel.isEmpty()
                        ) {

                            Toast.makeText(
                                requireContext(),
                                "Please fill in the required fields.",
                                Toast.LENGTH_SHORT
                            ).show()


                            return@setOnClickListener
                        }


                        // =================================================
                        // UPDATED DATA
                        // =================================================

                        val updates =
                            hashMapOf<String, Any>(

                                "fullName" to
                                        updatedName,

                                "studentId" to
                                        updatedStudentId,

                                "course" to
                                        updatedCourse,

                                "yearLevel" to
                                        updatedYearLevel,

                                "interest" to
                                        updatedInterest,

                                "contactNumber" to
                                        updatedContact
                            )


                        // =================================================
                        // UPDATE FIRESTORE
                        // =================================================

                        db.collection("users")
                            .document(uid)
                            .update(updates)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    requireContext(),
                                    "Profile updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()


                                // Refresh profile
                                loadStudentProfile()


                                dialog.dismiss()
                            }
                            .addOnFailureListener {

                                Toast.makeText(
                                    requireContext(),
                                    "Failed to update profile.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }


                dialog.show()
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Unable to open profile editor.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    // =========================================================
    // UPLOAD PROFILE PICTURE
    // =========================================================

    private fun uploadProfilePicture(
        uri: Uri
    ) {

        val currentUser =
            auth.currentUser


        if (currentUser == null) {
            return
        }


        val uid =
            currentUser.uid


        Toast.makeText(
            requireContext(),
            "Uploading profile picture...",
            Toast.LENGTH_SHORT
        ).show()


        // =====================================================
        // FIREBASE STORAGE LOCATION
        // =====================================================

        val imageRef =
            storage.reference
                .child("profilePictures")
                .child("$uid.jpg")


        // =====================================================
        // UPLOAD IMAGE
        // =====================================================

        imageRef.putFile(uri)
            .addOnSuccessListener {

                // Get download URL
                imageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->

                        // =================================================
                        // SAVE URL TO FIRESTORE
                        // =================================================

                        db.collection("users")
                            .document(uid)
                            .update(
                                "profileImageUrl",
                                downloadUri.toString()
                            )
                            .addOnSuccessListener {

                                Toast.makeText(
                                    requireContext(),
                                    "Profile picture updated!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {

                                Toast.makeText(
                                    requireContext(),
                                    "Picture uploaded but profile was not updated.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            }
            .addOnFailureListener {

                Toast.makeText(
                    requireContext(),
                    "Failed to upload profile picture.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    // =========================================================
    // DP HELPER
    // =========================================================

    private fun Int.dp(): Int {

        return (
                this *
                        resources.displayMetrics.density
                ).toInt()
    }
}