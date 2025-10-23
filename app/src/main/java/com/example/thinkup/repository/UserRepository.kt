package com.example.thinkup.repository

import android.content.Context
import com.example.thinkup.data.ThinkUpDatabase
import com.example.thinkup.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val context: Context) {

    private val database = ThinkUpDatabase.getDatabase(context)
    private val userDao = database.userDao()

    suspend fun register(user: User): Boolean = withContext(Dispatchers.IO) {
        try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                false // User already exists
            } else {
                userDao.insertUser(user)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(email: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            userDao.getUserByEmailAndPassword(email, password)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        try {
            userDao.getUserByEmail(email)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(user: User): Boolean = withContext(Dispatchers.IO) {
        try {
            userDao.updateUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteUser(email: String): Boolean = withContext(Dispatchers.IO) {
        try {
            userDao.deleteUserByEmail(email)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun logout() {
        // In a real app, you might want to clear some session data here
        // For now, we'll just return since the database persists user data
    }
}
