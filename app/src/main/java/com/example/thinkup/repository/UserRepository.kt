package com.example.thinkup.repository


import android.content.Context
import com.example.thinkup.model.User

class UserRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun register(user: User): Boolean {
        if (prefs.contains("email")) return false
        prefs.edit()
            .putString("name", user.name)
            .putString("email", user.email)
            .putString("password", user.password)
            .apply()
        return true
    }

    fun login(email: String, password: String): User? {
        val savedEmail = prefs.getString("email", null)
        val savedPass = prefs.getString("password", null)
        val savedName = prefs.getString("name", null)
        return if (email == savedEmail && password == savedPass)
            User(savedName ?: "", savedEmail ?: "", savedPass ?: "")
        else null
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
