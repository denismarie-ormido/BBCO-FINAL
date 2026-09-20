package com.example.kayara

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BrowseOrgAdapter(
    private var fullList: List<OrganizationModel>,
    private val onJoinClick: (OrganizationModel) -> Unit
) : RecyclerView.Adapter<BrowseOrgAdapter.BrowseViewHolder>() {

    private var displayedList: List<OrganizationModel> = fullList

    inner class BrowseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvAvatar: TextView =
            itemView.findViewById(R.id.tvBrowseAvatar)

        val tvName: TextView =
            itemView.findViewById(R.id.tvBrowseOrgName)

        val tvMeta: TextView =
            itemView.findViewById(R.id.tvBrowseOrgMeta)

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvBrowseStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrowseViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_browse_org,
                parent,
                false
            )

        return BrowseViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BrowseViewHolder,
        position: Int
    ) {

        val org = displayedList[position]

        holder.tvAvatar.text = org.shortCode

        holder.tvAvatar.background.setTint(
            android.graphics.Color.parseColor(
                org.colorStart
            )
        )

        holder.tvName.text = org.orgName

        holder.tvMeta.text =
            "${org.memberCount} members"

        when (org.category) {

            "joined" -> {

                holder.tvStatus.text = "JOINED"

                holder.tvStatus.setBackgroundResource(
                    R.drawable.bg_joined_tag
                )

                holder.tvStatus.setTextColor(
                    holder.itemView.context.getColor(
                        R.color.teal_deep_text
                    )
                )

                holder.itemView.setOnClickListener(null)
                holder.tvStatus.setOnClickListener(null)
            }

            "pending" -> {

                holder.tvStatus.text = "PENDING"

                holder.tvStatus.setBackgroundResource(
                    R.drawable.bg_pending_tag
                )

                holder.tvStatus.setTextColor(
                    holder.itemView.context.getColor(
                        R.color.pending_text
                    )
                )

                holder.itemView.setOnClickListener(null)
                holder.tvStatus.setOnClickListener(null)
            }

            else -> {

                holder.tvStatus.text = "JOIN"

                holder.tvStatus.setBackgroundResource(
                    R.drawable.bg_join_btn
                )

                holder.tvStatus.setTextColor(
                    holder.itemView.context.getColor(
                        R.color.white
                    )
                )

                holder.itemView.setOnClickListener {
                    onJoinClick(org)
                }

                holder.tvStatus.setOnClickListener {
                    onJoinClick(org)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return displayedList.size
    }

    fun updateList(newList: List<OrganizationModel>) {

        fullList = newList
        displayedList = newList

        notifyDataSetChanged()
    }

    fun filter(category: String) {

        displayedList = when (category) {

            "joined" ->
                fullList.filter {
                    it.category == "joined"
                }

            "pending" ->
                fullList.filter {
                    it.category == "pending"
                }

            else ->
                fullList
        }

        notifyDataSetChanged()
    }

    fun search(query: String) {

        displayedList =
            if (query.isBlank()) {

                fullList

            } else {

                fullList.filter {
                    it.orgName.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }

        notifyDataSetChanged()
    }
}