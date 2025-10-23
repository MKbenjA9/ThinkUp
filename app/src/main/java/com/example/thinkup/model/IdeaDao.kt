package com.example.thinkup.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface IdeaDao {
    
    @Query("SELECT * FROM ideas ORDER BY createdAt DESC")
    fun getAllIdeas(): Flow<List<Idea>>
    
    @Query("SELECT * FROM ideas WHERE id = :id")
    suspend fun getIdeaById(id: Long): Idea?
    
    @Query("SELECT * FROM ideas WHERE author = :author ORDER BY createdAt DESC")
    fun getIdeasByAuthor(author: String): Flow<List<Idea>>
    
    @Query("SELECT * FROM ideas WHERE category = :category ORDER BY createdAt DESC")
    fun getIdeasByCategory(category: String): Flow<List<Idea>>
    
    @Query("SELECT * FROM ideas ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomIdea(): Idea?
    
    @Query("SELECT * FROM ideas WHERE (lat BETWEEN :minLat AND :maxLat) AND (lng BETWEEN :minLng AND :maxLng)")
    fun getIdeasInArea(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): Flow<List<Idea>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: Idea): Long
    
    @Update
    suspend fun updateIdea(idea: Idea)
    
    @Delete
    suspend fun deleteIdea(idea: Idea)
    
    @Query("DELETE FROM ideas WHERE id = :id")
    suspend fun deleteIdeaById(id: Long)
    
    @Query("DELETE FROM ideas WHERE author = :author")
    suspend fun deleteIdeasByAuthor(author: String)
    
    @Query("DELETE FROM ideas")
    suspend fun deleteAllIdeas()
}
