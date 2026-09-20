package com.example.kayara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardFragment : Fragment() {

    private var selectedAudience = "everyone"
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var rvFeed: RecyclerView

    // Bag-ong: gi-declare diri sa taas aron ma-access sa tibuok class (naay initial sample data)
    private val feedPosts = mutableListOf(
        FeedPostModel(
            postType = "text",
            posterName = "Engr. Santos",
            posterRole = "Super Admin",
            timeAgo = "2 hours ago",
            audience = "everyone",
            textContent = "Reminder: General Assembly on September 5, 1:00 PM at the BISU Balilihan Gymnasium."
        ),
        FeedPostModel(
            postType = "photo",
            posterName = "Red Cross Youth",
            posterRole = "Admin",
            timeAgo = "3 hours ago",
            avatarInitials = "RCY",
            caption = "Blood Donation Drive 2026 — thank you to over 120 donors!",
            imageUrls = listOf("https://images.unsplash.com/photo-1615461066841-6116e61058f4?w=600&h=400&fit=crop"),
            likeCount = 214,
            commentCount = 18
        ),
        FeedPostModel(
            postType = "text",
            posterName = "Engr. Santos",
            posterRole = "Super Admin",
            timeAgo = "5 hours ago",
            audience = "admins_only",
            textContent = "All org advisers: please submit your updated officer roster on or before September 15."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val etComposeText = view.findViewById<EditText>(R.id.etComposeText)
        val toggleEveryone = view.findViewById<TextView>(R.id.toggleEveryone)
        val toggleAdminsOnly = view.findViewById<TextView>(R.id.toggleAdminsOnly)
        val btnSubmitPost = view.findViewById<TextView>(R.id.btnSubmitPost)
        rvFeed = view.findViewById(R.id.rvFeed)

        toggleEveryone.setOnClickListener {
            selectedAudience = "everyone"
            toggleEveryone.setBackgroundResource(R.drawable.bg_audience_toggle_active)
            toggleEveryone.setTextColor(resources.getColor(R.color.white, null))
            toggleAdminsOnly.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            toggleAdminsOnly.setTextColor(resources.getColor(R.color.text_gray, null))
        }

        toggleAdminsOnly.setOnClickListener {
            selectedAudience = "admins_only"
            toggleAdminsOnly.setBackgroundResource(R.drawable.bg_audience_toggle_active)
            toggleAdminsOnly.setTextColor(resources.getColor(R.color.white, null))
            toggleEveryone.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            toggleEveryone.setTextColor(resources.getColor(R.color.text_gray, null))
        }

        // BAG-ONG LOGIC: mag-dugang na sa listahan ug mag-refresh sa RecyclerView
        btnSubmitPost.setOnClickListener {
            val text = etComposeText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Please write something first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newPost = FeedPostModel(
                postType = "text",
                posterName = "Engr. Santos",
                posterRole = "Super Admin",
                timeAgo = "Just now",
                audience = selectedAudience,
                textContent = text
            )

            feedAdapter.addPost(newPost)
            rvFeed.scrollToPosition(0)

            // TODO: I-save pud sa Firestore diri: { text, audience, postedBy, timestamp }

            Toast.makeText(requireContext(), "Posted successfully!", Toast.LENGTH_SHORT).show()
            etComposeText.text.clear()
        }

        rvFeed.layoutManager = LinearLayoutManager(requireContext())
        feedAdapter = FeedAdapter(feedPosts)
        rvFeed.adapter = feedAdapter

        return view
    }
}