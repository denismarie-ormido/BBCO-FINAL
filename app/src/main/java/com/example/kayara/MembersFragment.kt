package com.example.kayara

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MembersFragment : Fragment() {

    private lateinit var memberAdapter: MemberAdapter

    private lateinit var chipAll: TextView
    private lateinit var chipOfficers: TextView
    private lateinit var chipPending: TextView

    private val db =
        FirebaseFirestore.getInstance()

    private val members =
        mutableListOf<MemberModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_members,
            container,
            false
        )

        // -----------------------------------------
        // VIEWS
        // -----------------------------------------

        val rvMembers =
            view.findViewById<RecyclerView>(
                R.id.rvMembers
            )

        val etSearchMember =
            view.findViewById<EditText>(
                R.id.etSearchMember
            )

        chipAll =
            view.findViewById(
                R.id.chipAllMembers
            )

        chipOfficers =
            view.findViewById(
                R.id.chipOfficers
            )

        chipPending =
            view.findViewById(
                R.id.chipPendingMembers
            )

        // -----------------------------------------
        // RECYCLER VIEW
        // -----------------------------------------

        rvMembers.layoutManager =
            LinearLayoutManager(requireContext())

        memberAdapter = MemberAdapter(
            members,

            onApprove = { position ->

                val member =
                    memberAdapter.getMemberAt(position)

                if (member != null) {
                    approveMember(member)
                }
            },

            onReject = { position ->

                val member =
                    memberAdapter.getMemberAt(position)

                if (member != null) {
                    rejectMember(member)
                }
            }
        )

        rvMembers.adapter =
            memberAdapter

        // -----------------------------------------
        // LOAD FIREBASE DATA
        // -----------------------------------------

        loadMembers()

        // -----------------------------------------
        // ALL
        // -----------------------------------------

        chipAll.setOnClickListener {

            setActiveChip(chipAll)

            memberAdapter.filter("all")
        }

        // -----------------------------------------
        // OFFICERS
        // -----------------------------------------

        chipOfficers.setOnClickListener {

            setActiveChip(chipOfficers)

            memberAdapter.filter("officer")
        }

        // -----------------------------------------
        // PENDING
        // -----------------------------------------

        chipPending.setOnClickListener {

            setActiveChip(chipPending)

            memberAdapter.filter("pending")
        }

        // -----------------------------------------
        // SEARCH
        // -----------------------------------------

        etSearchMember.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    memberAdapter.search(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        return view
    }

    // =================================================
    // LOAD MEMBERS FROM FIRESTORE
    // =================================================

    private fun loadMembers() {

        db.collection("memberships")
            .get()
            .addOnSuccessListener { result ->

                members.clear()

                // -------------------------------------
                // NO MEMBERS
                // -------------------------------------

                if (result.documents.isEmpty()) {

                    memberAdapter.updateList(
                        members
                    )

                    updateChipCounts()

                    return@addOnSuccessListener
                }

                // -------------------------------------
                // COUNT COMPLETED USER LOOKUPS
                // -------------------------------------

                var completed = 0

                val total =
                    result.documents.size

                for (document in result.documents) {

                    val studentId =
                        document.getString(
                            "studentId"
                        )

                    val status =
                        document.getString(
                            "status"
                        ) ?: "pending"

                    val organizationName =
                        document.getString(
                            "organizationName"
                        ) ?: "Organization"

                    val applicationNote =
                        document.getString(
                            "applicationNote"
                        ) ?: ""

                    val membershipId =
                        document.id

                    // ---------------------------------
                    // STUDENT ID MISSING
                    // ---------------------------------

                    if (studentId.isNullOrBlank()) {

                        completed++

                        if (completed == total) {

                            memberAdapter.updateList(
                                members
                            )

                            updateChipCounts()
                        }

                        continue
                    }

                    // ---------------------------------
                    // GET USER DATA
                    // users/{studentId}
                    // ---------------------------------

                    db.collection("users")
                        .document(studentId)
                        .get()
                        .addOnSuccessListener { userDocument ->

                            val fullName =
                                userDocument.getString(
                                    "fullName"
                                ) ?: "Unknown Student"

                            val course =
                                userDocument.getString(
                                    "course"
                                ) ?: ""

                            val yearLevel =
                                userDocument.getString(
                                    "yearLevel"
                                ) ?: ""

                            // ---------------------------------
                            // DETAILS
                            // ---------------------------------

                            val details =
                                when (status) {

                                    "pending" -> {

                                        "$organizationName • Application Pending"
                                    }

                                    "approved" -> {

                                        if (
                                            course.isNotBlank() &&
                                            yearLevel.isNotBlank()
                                        ) {

                                            "$course-$yearLevel • $organizationName"

                                        } else {

                                            "$organizationName • Member"
                                        }
                                    }

                                    "rejected" -> {

                                        "$organizationName • Rejected"
                                    }

                                    else -> {

                                        organizationName
                                    }
                                }

                            // ---------------------------------
                            // ROLE
                            // ---------------------------------

                            val role =
                                when (status) {

                                    "pending" ->
                                        "pending"

                                    "approved" ->
                                        "member"

                                    else ->
                                        "member"
                                }

                            // ---------------------------------
                            // ADD MEMBER
                            // ---------------------------------

                            members.add(
                                MemberModel(

                                    fullName =
                                        fullName,

                                    details =
                                        details,

                                    role =
                                        role,

                                    avatarColorStart =
                                        if (role == "pending") {
                                            "#F5A524"
                                        } else {
                                            "#0B2E6B"
                                        },

                                    avatarColorEnd =
                                        if (role == "pending") {
                                            "#E08A0F"
                                        } else {
                                            "#2A4E92"
                                        },

                                    applicationNote =
                                        applicationNote,

                                    membershipId =
                                        membershipId,

                                    organizationName =
                                        organizationName
                                )
                            )

                            // ---------------------------------
                            // FINISHED
                            // ---------------------------------

                            completed++

                            if (completed == total) {

                                memberAdapter.updateList(
                                    members
                                )

                                updateChipCounts()
                            }
                        }
                        .addOnFailureListener {

                            completed++

                            if (completed == total) {

                                memberAdapter.updateList(
                                    members
                                )

                                updateChipCounts()
                            }
                        }
                }
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    requireContext(),
                    "Failed to load members: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =================================================
    // APPROVE
    // =================================================

    private fun approveMember(
        member: MemberModel
    ) {

        if (member.membershipId.isBlank()) {

            Toast.makeText(
                requireContext(),
                "Membership record not found.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val membershipRef =
            db.collection("memberships")
                .document(member.membershipId)

        // -----------------------------------------
        // GET MEMBERSHIP FIRST
        // -----------------------------------------

        membershipRef.get()
            .addOnSuccessListener { membershipDocument ->

                val studentId =
                    membershipDocument.getString(
                        "studentId"
                    )

                val organizationName =
                    membershipDocument.getString(
                        "organizationName"
                    ) ?: member.organizationName

                if (studentId.isNullOrBlank()) {

                    Toast.makeText(
                        requireContext(),
                        "Student ID not found.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                // -----------------------------------------
                // UPDATE MEMBERSHIP
                // -----------------------------------------

                membershipRef.update(
                    mapOf(
                        "status" to "approved",
                        "approvedAt" to FieldValue.serverTimestamp()
                    )
                )
                    .addOnSuccessListener {

                        // ---------------------------------
                        // CREATE NOTIFICATION
                        // ---------------------------------

                        createNotification(
                            studentId = studentId,
                            title = "Membership Approved",
                            message = "Your application to $organizationName has been approved.",
                            type = "signup"
                        )

                        Toast.makeText(
                            requireContext(),
                            "${member.fullName} approved as member!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Reload Firebase data
                        loadMembers()
                    }
                    .addOnFailureListener { error ->

                        Toast.makeText(
                            requireContext(),
                            "Failed to approve: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    requireContext(),
                    "Failed to get membership: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =================================================
    // REJECT
    // =================================================

    private fun rejectMember(
        member: MemberModel
    ) {

        if (member.membershipId.isBlank()) {

            Toast.makeText(
                requireContext(),
                "Membership record not found.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val membershipRef =
            db.collection("memberships")
                .document(member.membershipId)

        // -----------------------------------------
        // GET MEMBERSHIP FIRST
        // -----------------------------------------

        membershipRef.get()
            .addOnSuccessListener { membershipDocument ->

                val studentId =
                    membershipDocument.getString(
                        "studentId"
                    )

                val organizationName =
                    membershipDocument.getString(
                        "organizationName"
                    ) ?: member.organizationName

                if (studentId.isNullOrBlank()) {

                    Toast.makeText(
                        requireContext(),
                        "Student ID not found.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                // -----------------------------------------
                // UPDATE MEMBERSHIP
                // -----------------------------------------

                membershipRef.update(
                    "status",
                    "rejected"
                )
                    .addOnSuccessListener {

                        // ---------------------------------
                        // CREATE NOTIFICATION
                        // ---------------------------------

                        createNotification(
                            studentId = studentId,
                            title = "Membership Application Update",
                            message = "Your application to $organizationName was not approved.",
                            type = "info"
                        )

                        Toast.makeText(
                            requireContext(),
                            "${member.fullName}'s application rejected.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Reload Firebase data
                        loadMembers()
                    }
                    .addOnFailureListener { error ->

                        Toast.makeText(
                            requireContext(),
                            "Failed to reject: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    requireContext(),
                    "Failed to get membership: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =================================================
    // CREATE NOTIFICATION
    // =================================================

    private fun createNotification(
        studentId: String,
        title: String,
        message: String,
        type: String
    ) {

        val notification = hashMapOf(

            "userId" to studentId,

            "title" to title,

            "message" to message,

            "type" to type,

            "read" to false,

            "createdAt" to FieldValue.serverTimestamp()
        )

        db.collection("notifications")
            .add(notification)
            .addOnFailureListener { error ->

                Toast.makeText(
                    requireContext(),
                    "Notification could not be created: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // =================================================
    // CHIP STYLE
    // =================================================

    private fun setActiveChip(
        activeChip: TextView
    ) {

        val allChips =
            listOf(
                chipAll,
                chipOfficers,
                chipPending
            )

        allChips.forEach { chip ->

            if (chip == activeChip) {

                chip.setBackgroundResource(
                    R.drawable.bg_chip_active
                )

                chip.setTextColor(
                    resources.getColor(
                        R.color.white,
                        null
                    )
                )

            } else {

                chip.setBackgroundResource(
                    R.drawable.bg_chip
                )

                chip.setTextColor(
                    resources.getColor(
                        R.color.text_gray,
                        null
                    )
                )
            }
        }
    }

    // =================================================
    // CHIP COUNTS
    // =================================================

    private fun updateChipCounts() {

        chipAll.text =
            "All (${members.size})"

        chipOfficers.text =
            "Officers (${members.count { it.role == "officer" }})"

        chipPending.text =
            "Pending (${members.count { it.role == "pending" }})"
    }
}