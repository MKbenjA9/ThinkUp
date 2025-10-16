package com.example.thinkup.model



data class Idea(
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val lat: Double,
    val lng: Double,
    val author: String,
    val createdAt: Long = System.currentTimeMillis()
)
