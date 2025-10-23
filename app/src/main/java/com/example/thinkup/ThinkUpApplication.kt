package com.example.thinkup

import android.app.Application
import com.example.thinkup.database.DatabaseInitializer
import com.example.thinkup.database.ThinkUpDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ThinkUpApplication : Application() {
    
    val database by lazy { ThinkUpDatabase.getDatabase(this) }
    
    // Application scope for database initialization
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize database with sample data
        applicationScope.launch {
            val initializer = DatabaseInitializer(this@ThinkUpApplication)
            initializer.initializeSampleData()
        }
    }
}
