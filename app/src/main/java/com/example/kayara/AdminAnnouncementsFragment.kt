package com.example.kayara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class AdminAnnouncementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_announcements, container, false)

        val toggleFromSuperAdmin = view.findViewById<TextView>(R.id.toggleFromSuperAdmin)
        val toggleMyPosts = view.findViewById<TextView>(R.id.toggleMyPosts)
        val viewFromSuperAdmin = view.findViewById<LinearLayout>(R.id.viewFromSuperAdmin)
        val viewMyPosts = view.findViewById<LinearLayout>(R.id.viewMyPosts)

        toggleFromSuperAdmin.setOnClickListener {
            toggleFromSuperAdmin.setBackgroundResource(R.drawable.bg_toggle_active)
            toggleFromSuperAdmin.setTextColor(resources.getColor(R.color.white, null))
            toggleMyPosts.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            toggleMyPosts.setTextColor(resources.getColor(R.color.text_gray, null))

            viewFromSuperAdmin.visibility = View.VISIBLE
            viewMyPosts.visibility = View.GONE
        }

        toggleMyPosts.setOnClickListener {
            toggleMyPosts.setBackgroundResource(R.drawable.bg_toggle_active)
            toggleMyPosts.setTextColor(resources.getColor(R.color.white, null))
            toggleFromSuperAdmin.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            toggleFromSuperAdmin.setTextColor(resources.getColor(R.color.text_gray, null))

            viewMyPosts.visibility = View.VISIBLE
            viewFromSuperAdmin.visibility = View.GONE
        }

        return view
    }
}