package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrganizationAdapter(
    private var fullList: List<OrganizationModel>,
    private val onOrgClick: (OrganizationModel) -> Unit
) : RecyclerView.Adapter<OrganizationAdapter.OrgViewHolder>() {

    // Ang listahan nga tinuod nga gipakita (pwede na-filter)
    private var displayedList: List<OrganizationModel> = fullList

    inner class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvOrgAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvOrgName)
        val tvMeta: TextView = itemView.findViewById(R.id.tvOrgMeta)
        val tvBadge: TextView = itemView.findViewById(R.id.tvOrgBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrgViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_organization, parent, false)
        return OrgViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrgViewHolder, position: Int) {
        val org = displayedList[position]
        holder.tvAvatar.text = org.shortCode
        holder.tvAvatar.background.setTint(android.graphics.Color.parseColor(org.colorStart))
        holder.tvName.text = org.orgName

        when (org.category) {
            "new_request" -> {
                holder.tvMeta.text = "New · Requested by ${org.adviserName}"
                holder.tvBadge.visibility = View.VISIBLE
                holder.tvBadge.text = "NEW REQUEST"
                holder.tvBadge.setBackgroundResource(R.drawable.bg_tag_new)
                holder.tvBadge.setTextColor(holder.itemView.context.getColor(R.color.coral_text))
            }
            "renewal_due" -> {
                holder.tvMeta.text = "${org.memberCount} members · Adviser: ${org.adviserName}"
                holder.tvBadge.visibility = View.VISIBLE
                holder.tvBadge.text = "RENEWAL DUE"
                holder.tvBadge.setBackgroundResource(R.drawable.bg_tag_pending)
                holder.tvBadge.setTextColor(holder.itemView.context.getColor(R.color.amber_text))
            }
            else -> {
                holder.tvMeta.text = "${org.memberCount} members · Adviser: ${org.adviserName}"
                holder.tvBadge.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener { onOrgClick(org) }
    }

    override fun getItemCount(): Int = displayedList.size

    // Bag-ong function — mao ni ang mo-filter sa listahan base sa category
    fun filter(category: String) {
        displayedList = when (category) {
            "new_request" -> fullList.filter { it.category == "new_request" }
            "renewal_due" -> fullList.filter { it.category == "renewal_due" }
            else -> fullList // "all"
        }
        notifyDataSetChanged()
    }

    // Bag-ong function — para sa search bar (optional nga gamiton)
    fun search(query: String) {
        displayedList = if (query.isBlank()) {
            fullList
        } else {
            fullList.filter { it.orgName.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}