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

class OrganizationsFragment : Fragment() {

    private lateinit var orgAdapter: OrganizationAdapter
    private lateinit var chipAll: TextView
    private lateinit var chipNewRequests: TextView
    private lateinit var chipRenewalDue: TextView

    private var currentFilter = "all"

    // TODO: I-replace ni sa Firestore query sa umaabot
    private val organizations = listOf(
        OrganizationModel("Supreme Student Government", "SSG", 212, "Ma'am Lopez", "normal", "#0B2E6B", "#2A4E92"),
        OrganizationModel("Red Cross Youth", "RCY", 98, "Ma'am Reyes", "normal", "#12B5A6", "#0C8E82"),
        OrganizationModel("IT Society", "ITS", 76, "Sir Torres", "renewal_due", "#F5A524", "#E08A0F"),
        OrganizationModel("Educators' Society", "EDS", 140, "Sir Bautista", "renewal_due", "#0B2E6B", "#2A4E92"),
        OrganizationModel("Peer Facilitators Society", "PFS", 0, "Ma'am Reyes", "new_request", "#EF5350", "#C63A37"),
        OrganizationModel("BS Agri-Forestry Society", "BAF", 0, "Sir Dela Cruz", "new_request", "#17C2AE", "#0C8E82")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_organizations, container, false)

        val rvOrganizations = view.findViewById<RecyclerView>(R.id.rvOrganizations)
        val etSearchOrg = view.findViewById<EditText>(R.id.etSearchOrg)
        val tvEmptyState = view.findViewById<TextView>(R.id.tvEmptyState)
        chipAll = view.findViewById(R.id.chipAll)
        chipNewRequests = view.findViewById(R.id.chipNewRequests)
        chipRenewalDue = view.findViewById(R.id.chipRenewalDue)

        rvOrganizations.layoutManager = LinearLayoutManager(requireContext())
        orgAdapter = OrganizationAdapter(organizations) { org ->
            Toast.makeText(requireContext(), "Opening ${org.orgName}...", Toast.LENGTH_SHORT).show()
            // TODO: mo-adto sa OrganizationDetailActivity nga naay full info
        }
        rvOrganizations.adapter = orgAdapter

        // I-update ang chip counts base sa tinuod nga data
        val newRequestCount = organizations.count { it.category == "new_request" }
        val renewalDueCount = organizations.count { it.category == "renewal_due" }
        chipAll.text = "All (${organizations.size})"
        chipNewRequests.text = "New Requests ($newRequestCount)"
        chipRenewalDue.text = "Renewal Due ($renewalDueCount)"

        // BAG-ONG LOGIC: functional nga chips
        chipAll.setOnClickListener {
            currentFilter = "all"
            setActiveChip(chipAll)
            orgAdapter.filter("all")
            checkEmptyState(tvEmptyState)
        }

        chipNewRequests.setOnClickListener {
            currentFilter = "new_request"
            setActiveChip(chipNewRequests)
            orgAdapter.filter("new_request")
            checkEmptyState(tvEmptyState)
        }

        chipRenewalDue.setOnClickListener {
            currentFilter = "renewal_due"
            setActiveChip(chipRenewalDue)
            orgAdapter.filter("renewal_due")
            checkEmptyState(tvEmptyState)
        }

        // Search functionality
        etSearchOrg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                orgAdapter.search(s.toString())
                checkEmptyState(tvEmptyState)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun setActiveChip(activeChip: TextView) {
        val allChips = listOf(chipAll, chipNewRequests, chipRenewalDue)
        allChips.forEach { chip ->
            if (chip == activeChip) {
                chip.setBackgroundResource(R.drawable.bg_chip_active)
                chip.setTextColor(resources.getColor(R.color.white, null))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip)
                chip.setTextColor(resources.getColor(R.color.text_gray, null))
            }
        }
    }

    private fun checkEmptyState(tvEmptyState: TextView) {
        val rv = view?.findViewById<RecyclerView>(R.id.rvOrganizations)
        tvEmptyState.visibility = if (orgAdapter.itemCount == 0) View.VISIBLE else View.GONE
        rv?.visibility = if (orgAdapter.itemCount == 0) View.GONE else View.VISIBLE
    }
}