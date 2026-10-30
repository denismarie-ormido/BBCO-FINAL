package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class SuperAdminApplicationAdapter(
    private var applications: List<AdminApplicationModel>,
    private val onViewClick: (AdminApplicationModel) -> Unit
) : RecyclerView.Adapter<SuperAdminApplicationAdapter.ApplicationViewHolder>() {

    inner class ApplicationViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvOrgInitial: TextView =
            itemView.findViewById(R.id.tvOrgInitial)

        val tvOrganizationName: TextView =
            itemView.findViewById(R.id.tvOrganizationName)

        val tvApplicantName: TextView =
            itemView.findViewById(R.id.tvApplicantName)

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvStatus)

        val tvEmail: TextView =
            itemView.findViewById(R.id.tvEmail)

        val tvDate: TextView =
            itemView.findViewById(R.id.tvDate)

        val btnViewApplication: TextView =
            itemView.findViewById(R.id.btnViewApplication)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ApplicationViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_pending_application,
                parent,
                false
            )

        return ApplicationViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ApplicationViewHolder,
        position: Int
    ) {

        val application = applications[position]

        // ORGANIZATION NAME
        holder.tvOrganizationName.text =
            application.organizationName.ifBlank {
                "Unnamed Organization"
            }

        // APPLICANT NAME
        holder.tvApplicantName.text =
            application.fullName.ifBlank {
                "Unknown Applicant"
            }

        // EMAIL
        holder.tvEmail.text =
            application.email.ifBlank {
                "No email provided"
            }

        // ORGANIZATION INITIAL
        holder.tvOrgInitial.text =
            getInitial(application.organizationName)

        // STATUS
        holder.tvStatus.text =
            application.status.uppercase(Locale.getDefault())

        // DATE
        holder.tvDate.text =
            formatDate(application.createdAt)

        // VIEW APPLICATION
        holder.btnViewApplication.setOnClickListener {
            onViewClick(application)
        }
    }

    override fun getItemCount(): Int {
        return applications.size
    }

    fun updateApplications(
        newApplications: List<AdminApplicationModel>
    ) {
        applications = newApplications
        notifyDataSetChanged()
    }

    private fun getInitial(
        organizationName: String
    ): String {

        val cleanName = organizationName.trim()

        if (cleanName.isEmpty()) {
            return "O"
        }

        return cleanName
            .substring(0, 1)
            .uppercase(Locale.getDefault())
    }

    private fun formatDate(
        timestamp: com.google.firebase.Timestamp?
    ): String {

        if (timestamp == null) {
            return "Submitted recently"
        }

        val date = timestamp.toDate()

        val formatter = SimpleDateFormat(
            "MMM dd, yyyy • hh:mm a",
            Locale.getDefault()
        )

        return "Submitted " + formatter.format(date)
    }
}