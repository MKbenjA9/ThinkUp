package com.example.thinkup.repository

import android.content.Context
import com.example.thinkup.data.ThinkUpDatabase
import com.example.thinkup.model.Idea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class IdeasRepository(private val context: Context) {
    
    private val database = ThinkUpDatabase.getDatabase(context)
    private val ideaDao = database.ideaDao()

    /* ===== CRUD ===== */

    suspend fun saveIdea(idea: Idea): Long = withContext(Dispatchers.IO) {
        try {
            ideaDao.insertIdea(idea)
        } catch (e: Exception) {
            -1L
        }
    }

    fun getAll(): Flow<List<Idea>> = ideaDao.getAllIdeas()

    fun getByAuthor(author: String): Flow<List<Idea>> = ideaDao.getIdeasByAuthor(author)

    suspend fun getRandom(): Idea? = withContext(Dispatchers.IO) {
        try {
            ideaDao.getRandomIdea()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getIdeaById(id: Long): Idea? = withContext(Dispatchers.IO) {
        try {
            ideaDao.getIdeaById(id)
        } catch (e: Exception) {
            null
        }
    }

    fun getIdeasByCategory(category: String): Flow<List<Idea>> = ideaDao.getIdeasByCategory(category)

    fun getIdeasInArea(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): Flow<List<Idea>> = 
        ideaDao.getIdeasInArea(minLat, maxLat, minLng, maxLng)

    suspend fun updateIdea(idea: Idea): Boolean = withContext(Dispatchers.IO) {
        try {
            ideaDao.updateIdea(idea)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteIdea(id: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            ideaDao.deleteIdeaById(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteAllByAuthor(author: String): Boolean = withContext(Dispatchers.IO) {
        try {
            ideaDao.deleteIdeasByAuthor(author)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearAll(): Boolean = withContext(Dispatchers.IO) {
        try {
            ideaDao.deleteAllIdeas()
            true
        } catch (e: Exception) {
            false
        }
    }
}
