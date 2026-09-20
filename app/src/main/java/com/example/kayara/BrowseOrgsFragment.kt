package com.example.kayara

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class BrowseOrgsFragment : Fragment() {

    private lateinit var orgAdapter: BrowseOrgAdapter

    private lateinit var chipAll: TextView
    private lateinit var chipJoined: TextView
    private lateinit var chipPending: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val organizations = listOf(

        OrganizationModel(
            "Aspiring Criminologist of the Philippines Society",
            "ACOPS",
            54,
            "Organization Adviser",
            "available",
            "#263238",
            "#455A64"
        ),

        OrganizationModel(
            "BISU Balilihan Choir",
            "BBC",
            32,
            "Organization Adviser",
            "available",
            "#00838F",
            "#006064"
        ),

        OrganizationModel(
            "Chosen Generation Organization",
            "CGO",
            47,
            "Organization Adviser",
            "available",
            "#7B1FA2",
            "#6A1B9A"
        ),

        OrganizationModel(
            "Computer Science Guild",
            "CSG",
            68,
            "Organization Adviser",
            "available",
            "#1976D2",
            "#1565C0"
        ),

        OrganizationModel(
            "First Aiders Guild",
            "FAG",
            41,
            "Organization Adviser",
            "available",
            "#D32F2F",
            "#B71C1C"
        ),

        OrganizationModel(
            "Mayors League Organizations",
            "MLO",
            38,
            "Organization Adviser",
            "available",
            "#F9A825",
            "#F57F17"
        ),

        OrganizationModel(
            "Peer Facilitator",
            "PF",
            29,
            "Organization Adviser",
            "available",
            "#00897B",
            "#00695C"
        ),

        OrganizationModel(
            "Prime Synergy",
            "PS",
            36,
            "Organization Adviser",
            "available",
            "#8E24AA",
            "#6A1B9A"
        ),

        OrganizationModel(
            "Society of Electrical Technology",
            "SET",
            45,
            "Organization Adviser",
            "available",
            "#EF6C00",
            "#E65100"
        ),

        OrganizationModel(
            "Supreme Student Government",
            "SSG",
            85,
            "Organization Adviser",
            "available",
            "#0D47A1",
            "#1565C0"
        ),

        OrganizationModel(
            "The Campus Access",
            "TCA",
            27,
            "Organization Adviser",
            "available",
            "#00838F",
            "#006064"
        ),

        OrganizationModel(
            "World Changes",
            "WC",
            34,
            "Organization Adviser",
            "available",
            "#43A047",
            "#2E7D32"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.fragment_browse_orgs,
            container,
            false
        )

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val rvBrowseOrgs =
            view.findViewById<RecyclerView>(
                R.id.rvBrowseOrgs
            )

        val etSearch =
            view.findViewById<EditText>(
                R.id.etSearchOrgStudent
            )

        chipAll =
            view.findViewById(
                R.id.chipAllStudentOrgs
            )

        chipJoined =
            view.findViewById(
                R.id.chipJoined
            )

        chipPending =
            view.findViewById(
                R.id.chipPendingOrgs
            )

        // RecyclerView
        rvBrowseOrgs.layoutManager =
            LinearLayoutManager(requireContext())

        orgAdapter = BrowseOrgAdapter(
            organizations
        ) { org ->

            showJoinDialog(org)
        }

        rvBrowseOrgs.adapter = orgAdapter

        // Load student's memberships
        loadMemberships()

        // All
        chipAll.setOnClickListener {

            setActiveChip(chipAll)

            orgAdapter.filter("all")
        }

        // Joined
        chipJoined.setOnClickListener {

            setActiveChip(chipJoined)

            orgAdapter.filter("joined")
        }

        // Pending
        chipPending.setOnClickListener {

            setActiveChip(chipPending)

            orgAdapter.filter("pending")
        }

        // Search
        etSearch.addTextChangedListener(
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

                    orgAdapter.search(
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

    // =========================================================
    // LOAD MEMBERSHIPS FROM FIRESTORE
    // =========================================================

    private fun loadMemberships() {

        val currentUser = auth.currentUser

        if (currentUser == null) {
            return
        }

        val uid = currentUser.uid

        db.collection("memberships")
            .whereEqualTo("studentId", uid)
            .get()
            .addOnSuccessListener { snapshot ->

                val membershipStatus =
                    mutableMapOf<String, String>()

                for (document in snapshot.documents) {

                    val organizationId =
                        document.getString(
                            "organizationId"
                        )

                    val status =
                        document.getString(
                            "status"
                        )

                    if (
                        !organizationId.isNullOrEmpty() &&
                        !status.isNullOrEmpty()
                    ) {

                        membershipStatus[
                            organizationId
                        ] = status
                    }
                }

                val updatedOrganizations =
                    organizations.map { org ->

                        val status =
                            membershipStatus[
                                org.shortCode
                            ]

                        when (status) {

                            "approved" ->
                                org.copy(
                                    category = "joined"
                                )

                            "pending" ->
                                org.copy(
                                    category = "pending"
                                )

                            else ->
                                org.copy(
                                    category = "available"
                                )
                        }
                    }

                orgAdapter.updateList(
                    updatedOrganizations
                )

                updateChipCounts(
                    updatedOrganizations
                )
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
    // UPDATE CHIP COUNTS
    // =========================================================

    private fun updateChipCounts(
        list: List<OrganizationModel>
    ) {

        val joinedCount =
            list.count {
                it.category == "joined"
            }

        val pendingCount =
            list.count {
                it.category == "pending"
            }

        chipAll.text =
            "All (${list.size})"

        chipJoined.text =
            "Joined ($joinedCount)"

        chipPending.text =
            "Pending ($pendingCount)"
    }

    // =========================================================
    // CHIP DESIGN
    // =========================================================

    private fun setActiveChip(
        activeChip: TextView
    ) {

        val allChips =
            listOf(
                chipAll,
                chipJoined,
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

    // =========================================================
    // JOIN DIALOG
    // =========================================================

    private fun showJoinDialog(
        org: OrganizationModel
    ) {

        val dialog =
            Dialog(requireContext())

        val dialogView =
            LayoutInflater.from(
                requireContext()
            ).inflate(
                R.layout.dialog_join_org,
                null
            )

        dialog.setContentView(dialogView)

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                android.graphics.Color.TRANSPARENT
            )
        )

        dialog.window?.setGravity(
            android.view.Gravity.BOTTOM
        )

        val tvTitle =
            dialogView.findViewById<TextView>(
                R.id.tvJoinDialogTitle
            )

        val recruitmentBanner =
            dialogView.findViewById<LinearLayout>(
                R.id.recruitmentInfoBanner
            )

        val tvRecruitmentText =
            dialogView.findViewById<TextView>(
                R.id.tvRecruitmentInfoText
            )

        val etJoinMessage =
            dialogView.findViewById<EditText>(
                R.id.etJoinMessage
            )

        val btnCancel =
            dialogView.findViewById<TextView>(
                R.id.btnCancelJoin
            )

        val btnSubmit =
            dialogView.findViewById<TextView>(
                R.id.btnSubmitJoin
            )

        tvTitle.text =
            "Join ${org.orgName}"

        recruitmentBanner.visibility =
            View.GONE

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {

            val message =
                etJoinMessage.text
                    .toString()
                    .trim()

            submitJoinRequest(
                org = org,
                message = message,
                dialog = dialog,
                button = btnSubmit
            )
        }

        dialog.show()

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    // =========================================================
    // SAVE JOIN REQUEST TO FIRESTORE
    // =========================================================

    private fun submitJoinRequest(
        org: OrganizationModel,
        message: String,
        dialog: Dialog,
        button: TextView
    ) {

        val currentUser = auth.currentUser

        if (currentUser == null) {

            Toast.makeText(
                requireContext(),
                "Please login again.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val uid = currentUser.uid

        // Prevent duplicate request
        val membershipId =
            "${uid}_${org.shortCode}"

        button.isEnabled = false
        button.alpha = 0.6f

        val membershipData =
            hashMapOf<String, Any>(

                "studentId" to uid,

                "organizationId" to
                        org.shortCode,

                "organizationName" to
                        org.orgName,

                "shortCode" to
                        org.shortCode,

                "adviserName" to
                        org.adviserName,

                "status" to
                        "pending",

                "applicationNote" to
                        message,

                "dateApplied" to
                        FieldValue.serverTimestamp()
            )

        db.collection("memberships")
            .document(membershipId)
            .set(membershipData)
            .addOnSuccessListener {

                Toast.makeText(
                    requireContext(),
                    "Request to join ${org.orgName} sent!",
                    Toast.LENGTH_LONG
                ).show()

                dialog.dismiss()

                // Refresh organization statuses
                loadMemberships()
            }
            .addOnFailureListener {

                button.isEnabled = true
                button.alpha = 1f

                Toast.makeText(
                    requireContext(),
                    "Failed to send join request.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}