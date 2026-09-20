package com.example.kayara

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ManageAdminsActivity : AppCompatActivity() {

    private lateinit var pendingAdapter: PendingAdminAdapter
    private lateinit var tvPendingCount: TextView

    private val pendingList = mutableListOf(
        PendingAdminModel("Ma'am Reyes", "Peer Facilitators Society", "mreyes.pfs"),
        PendingAdminModel("Sir Dela Cruz", "BS Agri-Forestry Society", "jdelacruz.baf")
    )

    private val activeList = listOf(
        ActiveAdminModel("Ma'am Reyes", "Red Cross Youth"),
        ActiveAdminModel("Sir Torres", "IT Society"),
        ActiveAdminModel("Ma'am Lopez", "Supreme Student Government")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_admins)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        tvPendingCount = findViewById(R.id.tvPendingCount)

        // Pending admins RecyclerView
        val rvPendingAdmins = findViewById<RecyclerView>(R.id.rvPendingAdmins)
        rvPendingAdmins.layoutManager = LinearLayoutManager(this)

        pendingAdapter = PendingAdminAdapter(
            pendingList,
            onApprove = { position ->
                pendingList[position].status = "approved"
                pendingAdapter.notifyItemChanged(position)
                updatePendingCount()
                // TODO: i-activate ang admin account sa database + i-send notification
                Toast.makeText(
                    this,
                    "${pendingList[position].fullName} approved. Admin notified.",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onReject = { position ->
                pendingList[position].status = "rejected"
                pendingAdapter.notifyItemChanged(position)
                updatePendingCount()
                // TODO: i-mark rejected sa database + i-send notification (with reason kung naa)
                Toast.makeText(
                    this,
                    "${pendingList[position].fullName} rejected. Admin notified.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        rvPendingAdmins.adapter = pendingAdapter

        // Active admins RecyclerView
        val rvActiveAdmins = findViewById<RecyclerView>(R.id.rvActiveAdmins)
        rvActiveAdmins.layoutManager = LinearLayoutManager(this)
        rvActiveAdmins.adapter = ActiveAdminAdapter(activeList)
    }

    private fun updatePendingCount() {
        val stillPending = pendingList.count { it.status == "pending" }
        tvPendingCount.text = "Pending Approval ($stillPending)"
    }
}