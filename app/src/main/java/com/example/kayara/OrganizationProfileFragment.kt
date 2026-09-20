package com.example.kayara

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrganizationProfileFragment : Fragment() {

    private lateinit var feedAdapter: FeedAdapter
    private lateinit var rvOrgProfileFeed: RecyclerView

    private lateinit var contentPosts: LinearLayout
    private lateinit var contentAbout: LinearLayout
    private lateinit var contentMembers: LinearLayout

    private lateinit var tabPosts: LinearLayout
    private lateinit var tabAbout: LinearLayout
    private lateinit var tabMembers: LinearLayout

    private lateinit var underlinePosts: View
    private lateinit var underlineAbout: View
    private lateinit var underlineMembers: View

    private var imgCoverPhoto: android.widget.ImageView? = null
    private var imgOrgAvatarPhoto: android.widget.ImageView? = null
    private var tvOrgAvatarInitials: TextView? = null

    private val pickAvatarLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                imgOrgAvatarPhoto?.setImageURI(uri)
                imgOrgAvatarPhoto?.visibility = View.VISIBLE
                tvOrgAvatarInitials?.visibility = View.GONE
            }
        }

    private val pickCoverLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) imgCoverPhoto?.setImageURI(uri)
        }

    private val editProfileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                view?.findViewById<TextView>(R.id.tvOrgName)?.text = data?.getStringExtra("orgName")
                view?.findViewById<TextView>(R.id.tvOrgCategory)?.text = data?.getStringExtra("category")
                view?.findViewById<TextView>(R.id.tvOrgDescription)?.text = data?.getStringExtra("description")
                view?.findViewById<TextView>(R.id.tvAboutDescription)?.text = data?.getStringExtra("description")
                view?.findViewById<TextView>(R.id.tvAboutFounded)?.text = data?.getStringExtra("founded")
            }
        }

    // TEMPORARY: gikuha una nako ang photo post aron ma-isolate kung Glide/photo ang problema
    private val ownPosts = mutableListOf(
        FeedPostModel(
            postType = "text",
            posterName = "Red Cross Youth",
            posterRole = "You",
            timeAgo = "Yesterday",
            textContent = "Weekly meeting moved to Friday, 4:00 PM at Room 202."
        )
    )

    private val orgMembers = mutableListOf(
        MemberModel("Juliana Dizon", "BSN-3A - Secretary", "officer", "#2E9DF7", "#4FC3F7"),
        MemberModel("Krystal Lim", "BSIT-1A - Member", "member", "#0B2E6B", "#2A4E92")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = try {
            inflater.inflate(R.layout.fragment_organization_profile, container, false)
        } catch (e: Exception) {
            Log.e("OrgProfile", "Inflate failed: ${e.message}", e)
            Toast.makeText(requireContext(), "Layout error: ${e.message}", Toast.LENGTH_LONG).show()
            return null
        }

        try {
            imgCoverPhoto = view.findViewById(R.id.imgCoverPhoto)
            imgOrgAvatarPhoto = view.findViewById(R.id.imgOrgAvatarPhoto)
            tvOrgAvatarInitials = view.findViewById(R.id.tvOrgAvatarInitials)

            view.findViewById<TextView>(R.id.tvOrgName)?.text = "Red Cross Youth"
            view.findViewById<TextView>(R.id.tvOrgCategory)?.text = "Student Organization"
            view.findViewById<TextView>(R.id.tvOrgDescription)?.text =
                "Promoting volunteerism, leadership, community service, and youth development."
            tvOrgAvatarInitials?.text = "RCY"
            view.findViewById<TextView>(R.id.tvStatMembers)?.text = orgMembers.size.toString()
            view.findViewById<TextView>(R.id.tvStatPosts)?.text = ownPosts.size.toString()
            view.findViewById<TextView>(R.id.tvStatEvents)?.text = "5"

            // Change cover photo
            view.findViewById<FrameLayout>(R.id.btnChangeCover)?.setOnClickListener {
                pickCoverLauncher.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }

            // Change avatar/logo
            view.findViewById<FrameLayout>(R.id.btnChangeAvatar)?.setOnClickListener {
                pickAvatarLauncher.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }

            view.findViewById<LinearLayout>(R.id.btnEditProfile)?.setOnClickListener {
                editProfileLauncher.launch(Intent(requireContext(), EditOrgProfileActivity::class.java))
            }

            contentPosts = view.findViewById(R.id.contentPosts)
            contentAbout = view.findViewById(R.id.contentAbout)
            contentMembers = view.findViewById(R.id.contentMembers)
            tabPosts = view.findViewById(R.id.tabPosts)
            tabAbout = view.findViewById(R.id.tabAbout)
            tabMembers = view.findViewById(R.id.tabMembers)
            underlinePosts = view.findViewById(R.id.underlinePosts)
            underlineAbout = view.findViewById(R.id.underlineAbout)
            underlineMembers = view.findViewById(R.id.underlineMembers)

            tabPosts.setOnClickListener { showTab("posts") }
            tabAbout.setOnClickListener { showTab("about") }
            tabMembers.setOnClickListener { showTab("members") }

            rvOrgProfileFeed = view.findViewById(R.id.rvOrgProfileFeed)
            rvOrgProfileFeed.layoutManager = LinearLayoutManager(requireContext())
            feedAdapter = FeedAdapter(ownPosts)
            rvOrgProfileFeed.adapter = feedAdapter

            val etCompose = view.findViewById<EditText>(R.id.etOrgProfileCompose)
            etCompose?.setOnEditorActionListener { textView, _, _ ->
                val text = textView.text.toString().trim()
                if (text.isNotEmpty()) {
                    val newPost = FeedPostModel(
                        postType = "text",
                        posterName = "Red Cross Youth",
                        posterRole = "You",
                        timeAgo = "Just now",
                        textContent = text
                    )
                    feedAdapter.addPost(newPost)
                    rvOrgProfileFeed.scrollToPosition(0)
                    view.findViewById<TextView>(R.id.tvStatPosts)?.text = ownPosts.size.toString()
                    textView.text = ""
                }
                true
            }

            val rvOrgProfileMembers = view.findViewById<RecyclerView>(R.id.rvOrgProfileMembers)
            rvOrgProfileMembers.layoutManager = LinearLayoutManager(requireContext())
            rvOrgProfileMembers.adapter = MemberAdapter(
                orgMembers,
                onApprove = { },
                onReject = { }
            )

        } catch (e: Exception) {
            Log.e("OrgProfile", "Setup failed: ${e.message}", e)
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }

        return view
    }

    private fun showTab(tab: String) {
        contentPosts.visibility = if (tab == "posts") View.VISIBLE else View.GONE
        contentAbout.visibility = if (tab == "about") View.VISIBLE else View.GONE
        contentMembers.visibility = if (tab == "members") View.VISIBLE else View.GONE

        underlinePosts.setBackgroundResource(
            if (tab == "posts") R.drawable.bg_tab_underline_active else R.color.card_border
        )
        underlineAbout.setBackgroundResource(
            if (tab == "about") R.drawable.bg_tab_underline_active else R.color.card_border
        )
        underlineMembers.setBackgroundResource(
            if (tab == "members") R.drawable.bg_tab_underline_active else R.color.card_border
        )

        setTabTextColor(tabPosts, tab == "posts")
        setTabTextColor(tabAbout, tab == "about")
        setTabTextColor(tabMembers, tab == "members")
    }

    private fun setTabTextColor(tabLayout: LinearLayout, isActive: Boolean) {
        val label = tabLayout.getChildAt(0) as? TextView
        label?.setTextColor(
            resources.getColor(if (isActive) R.color.dark_blue else R.color.text_gray, null)
        )
    }
}