package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PendingAdminAdapter(
    private val pendingList: MutableList<PendingAdminModel>,
    private val onApprove: (Int) -> Unit,
    private val onReject: (Int) -> Unit
) : RecyclerView.Adapter<PendingAdminAdapter.PendingViewHolder>() {

    inner class PendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInitials: TextView = itemView.findViewById(R.id.tvAdminInitials)
        val tvName: TextView = itemView.findViewById(R.id.tvAdminName)
        val tvOrg: TextView = itemView.findViewById(R.id.tvAdminOrg)
        val actionRow: LinearLayout = itemView.findViewById(R.id.adminActionRow)
        val tvStatusResult: TextView = itemView.findViewById(R.id.tvAdminStatusResult)
        val btnApprove: TextView = itemView.findViewById(R.id.btnApproveAdmin)
        val btnReject: TextView = itemView.findViewById(R.id.btnRejectAdmin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pending_admin, parent, false)
        return PendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        val admin = pendingList[position]
        holder.tvInitials.text = getInitials(admin.fullName)
        holder.tvName.text = admin.fullName
        holder.tvOrg.text = "${admin.orgName} · ${admin.username}"

        when (admin.status) {
            "approved" -> {
                holder.actionRow.visibility = View.GONE
                holder.tvStatusResult.visibility = View.VISIBLE
                holder.tvStatusResult.text = "✓ Approved"
                holder.tvStatusResult.setTextColor(holder.itemView.context.getColor(R.color.teal_deep))
                holder.tvStatusResult.setBackgroundResource(R.drawable.bg_tag_active)
            }
            "rejected" -> {
                holder.actionRow.visibility = View.GONE
                holder.tvStatusResult.visibility = View.VISIBLE
                holder.tvStatusResult.text = "✕ Rejected"
                holder.tvStatusResult.setTextColor(holder.itemView.context.getColor(R.color.coral_text))
                holder.tvStatusResult.setBackgroundResource(R.drawable.bg_tag_overdue)
            }
            else -> {
                holder.actionRow.visibility = View.VISIBLE
                holder.tvStatusResult.visibility = View.GONE
            }
        }

        holder.btnApprove.setOnClickListener { onApprove(holder.adapterPosition) }
        holder.btnReject.setOnClickListener { onReject(holder.adapterPosition) }
    }

    override fun getItemCount(): Int = pendingList.size

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else name.take(2).uppercase()
    }
}