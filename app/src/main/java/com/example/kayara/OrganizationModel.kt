package com.example.kayara

data class OrganizationModel(

    val orgName: String = "",

    val shortCode: String = "",

    val memberCount: Int = 0,


    // =====================================================
    // ADVISERS
    // =====================================================

    /*
     * Old compatibility field.
     * Existing Firestore organizations may still have this.
     */
    val adviserName: String = "",

    /*
     * New primary adviser.
     */
    val adviserName1: String = "",

    /*
     * Optional second adviser.
     */
    val adviserName2: String = "",


    val category: String = "normal",

    val colorStart: String = "#1E5FD9",

    val colorEnd: String = "#3FA9F5",

    val recruitmentInfo: String = "",

    val organizationId: String = "",


    // Organization profile picture
    val avatarUrl: String = "",


    // Recruitment status
    val recruitmentOpen: Boolean = true,


    // 0 = walay limit
    val recruitmentLimit: Int = 0,


    // "open" / "closed" / "full"
    val recruitmentStatus: String = "open"
)