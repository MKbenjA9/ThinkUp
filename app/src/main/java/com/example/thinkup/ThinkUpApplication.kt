package com.example.thinkup

import android.app.Application
import com.example.thinkup.data.ThinkUpDatabase

class ThinkUpApplication : Application() {
    
    val database by lazy { ThinkUpDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()

    }
}
