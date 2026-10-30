package com.example.kayara

data class EventModel(

    val eventId: String = "",

    val organizationId: String = "",

    val organizationName: String = "",

    val title: String = "",

    val description: String = "",

    val date: String = "",

    val time: String = "",

    val venue: String = "",

    val createdBy: String = "",

    val status: String = "upcoming",

    val createdAt: Long = 0L
)