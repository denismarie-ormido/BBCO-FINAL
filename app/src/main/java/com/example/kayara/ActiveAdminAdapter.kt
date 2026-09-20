package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ActiveAdminAdapter(
    private val activeList: List<ActiveAdminModel>
) : RecyclerView.Adapter<ActiveAdminAdapter.ActiveViewHolder>() {

    inner class ActiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvInitials: TextView = itemView.findViewById(R.id.tvActiveInitials)
        val tvName: TextView = itemView.findViewById(R.id.tvActiveName)
        val tvOrg: TextView = itemView.findViewById(R.id.tvActiveOrg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_active_admin, parent, false)
        return ActiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActiveViewHolder, position: Int) {
        val admin = activeList[position]
        holder.tvInitials.text = getInitials(admin.fullName)
        holder.tvName.text = admin.fullName
        holder.tvOrg.text = admin.orgName
    }

    override fun getItemCount(): Int = activeList.size

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else name.take(2).uppercase()
    }
}