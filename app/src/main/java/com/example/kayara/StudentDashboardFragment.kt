package com.example.kayara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudentDashboardFragment : Fragment() {

    // TODO: I-replace ni sa Firestore query
    // (audience="everyone" gikan sa Super Admin) + (posts sa orgs nga na-joinan)
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
            posterRole = "Org",
            timeAgo = "3 hours ago",
            avatarInitials = "RCY",
            caption = "Blood Donation Drive 2026 — thank you to over 120 donors!",
            imageUrls = listOf("https://images.unsplash.com/photo-1615461066841-6116e61058f4?w=600&h=400&fit=crop"),
            likeCount = 214,
            commentCount = 18
        ),
        FeedPostModel(
            postType = "text",
            posterName = "IT Society",
            posterRole = "Org",
            timeAgo = "Yesterday",
            textContent = "Coding Bootcamp registration is now open! Sign-ups close September 10."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_dashboard, container, false)

        val rvFeed = view.findViewById<RecyclerView>(R.id.rvStudentFeed)
        rvFeed.layoutManager = LinearLayoutManager(requireContext())
        rvFeed.adapter = FeedAdapter(feedPosts)

        view.findViewById<LinearLayout>(R.id.btnJoinOrgShortcut).setOnClickListener {
            // TODO: mo-navigate sa Organizations tab (via bottom nav)
            (activity as? StudentDashboardActivity)?.navigateToOrgsTab()
        }

        return view
    }
}