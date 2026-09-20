package com.example.kayara

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(
    private var fullList: MutableList<MemberModel>,
    private val onApprove: (Int) -> Unit,
    private val onReject: (Int) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    private var displayedList: MutableList<MemberModel> =
        fullList.toMutableList()

    inner class MemberViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvAvatar: TextView =
            itemView.findViewById(R.id.tvMemberAvatar)

        val tvName: TextView =
            itemView.findViewById(R.id.tvMemberName)

        val tvDetails: TextView =
            itemView.findViewById(R.id.tvMemberDetails)

        val tvRoleTag: TextView =
            itemView.findViewById(R.id.tvMemberRoleTag)

        val btnApprove: ImageView =
            itemView.findViewById(R.id.btnApproveMember)

        val btnReject: ImageView =
            itemView.findViewById(R.id.btnRejectMember)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_member,
                parent,
                false
            )

        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MemberViewHolder,
        position: Int
    ) {

        val member = displayedList[position]

        // -----------------------------------------
        // NAME
        // -----------------------------------------

        holder.tvAvatar.text =
            getInitials(member.fullName)

        holder.tvAvatar.background.setTint(
            Color.parseColor(
                member.avatarColorStart
            )
        )

        holder.tvName.text =
            member.fullName

        holder.tvDetails.text =
            member.details

        // -----------------------------------------
        // APPLICATION NOTE
        // -----------------------------------------

        val tvNote =
            holder.itemView.findViewById<TextView>(
                R.id.tvApplicationNote
            )

        if (
            member.applicationNote.isNotBlank() &&
            member.role == "pending"
        ) {

            tvNote.visibility = View.VISIBLE

            tvNote.text =
                "\uD83D\uDCAC \"${member.applicationNote}\""

        } else {

            tvNote.visibility = View.GONE
        }

        // -----------------------------------------
        // ROLE / STATUS
        // -----------------------------------------

        when (member.role) {

            "officer" -> {

                holder.tvRoleTag.text =
                    "OFFICER"

                holder.tvRoleTag.setBackgroundResource(
                    R.drawable.bg_role_officer
                )

                holder.tvRoleTag.setTextColor(
                    holder.itemView.context.getColor(
                        R.color.blue_deep
                    )
                )

                holder.btnApprove.visibility =
                    View.GONE

                holder.btnReject.visibility =
                    View.GONE
            }

            "pending" -> {

                holder.tvRoleTag.text =
                    "PENDING"

                holder.tvRoleTag.setBackgroundResource(
                    R.drawable.bg_tag_pending
                )

                holder.tvRoleTag.setTextColor(
                    holder.itemView.context.getColor(
                        R.color.amber_text
                    )
                )

                holder.btnApprove.visibility =
                    View.VISIBLE

                holder.btnReject.visibility =
                    View.VISIBLE
            }

            else -> {

                holder.tvRoleTag.text =
                    "MEMBER"

                holder.tvRoleTag.setBackgroundResource(
                    R.drawable.bg_role_member
                )

                holder.tvRoleTag.setTextColor(
                    holder.itemView.context.getColor(
                        R.color.text_gray
                    )
                )

                holder.btnApprove.visibility =
                    View.GONE

                holder.btnReject.visibility =
                    View.GONE
            }
        }

        // -----------------------------------------
        // APPROVE
        // -----------------------------------------

        holder.btnApprove.setOnClickListener {

            val adapterPosition =
                holder.bindingAdapterPosition

            if (
                adapterPosition != RecyclerView.NO_POSITION
            ) {
                onApprove(adapterPosition)
            }
        }

        // -----------------------------------------
        // REJECT
        // -----------------------------------------

        holder.btnReject.setOnClickListener {

            val adapterPosition =
                holder.bindingAdapterPosition

            if (
                adapterPosition != RecyclerView.NO_POSITION
            ) {
                onReject(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return displayedList.size
    }

    // ---------------------------------------------
    // GET MEMBER
    // ---------------------------------------------

    fun getMemberAt(position: Int): MemberModel? {

        return if (
            position >= 0 &&
            position < displayedList.size
        ) {
            displayedList[position]
        } else {
            null
        }
    }

    // ---------------------------------------------
    // UPDATE LIST
    // ---------------------------------------------

    fun updateList(
        newList: MutableList<MemberModel>
    ) {

        fullList = newList
        displayedList = newList.toMutableList()

        notifyDataSetChanged()
    }

    // ---------------------------------------------
    // FILTER
    // ---------------------------------------------

    fun filter(role: String) {

        displayedList = when (role) {

            "officer" -> {

                fullList
                    .filter {
                        it.role == "officer"
                    }
                    .toMutableList()
            }

            "pending" -> {

                fullList
                    .filter {
                        it.role == "pending"
                    }
                    .toMutableList()
            }

            else -> {

                fullList.toMutableList()
            }
        }

        notifyDataSetChanged()
    }

    // ---------------------------------------------
    // SEARCH
    // ---------------------------------------------

    fun search(query: String) {

        displayedList = if (query.isBlank()) {

            fullList.toMutableList()

        } else {

            fullList
                .filter {

                    it.fullName.contains(
                        query,
                        ignoreCase = true
                    ) ||

                            it.organizationName.contains(
                                query,
                                ignoreCase = true
                            )
                }
                .toMutableList()
        }

        notifyDataSetChanged()
    }

    // ---------------------------------------------
    // INITIALS
    // ---------------------------------------------

    private fun getInitials(
        name: String
    ): String {

        val parts =
            name.trim()
                .split(" ")
                .filter { it.isNotBlank() }

        return if (parts.size >= 2) {

            "${parts[0][0]}${parts[1][0]}"
                .uppercase()

        } else {

            name
                .take(2)
                .uppercase()
        }
    }
}