package com.example.thinkup.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ideas")
data class Idea(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val lat: Double,
    val lng: Double,
    val author: String,
    val createdAt: Long = System.currentTimeMillis()
)
