package com.example.thinkup.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.thinkup.model.Idea
import com.example.thinkup.model.User
import com.example.thinkup.model.IdeaDao
import com.example.thinkup.model.UserDao

@Database(
    entities = [User::class, Idea::class],
    version = 1,
    exportSchema = false
)
abstract class ThinkUpDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun ideaDao(): IdeaDao
    
    companion object {
        @Volatile
        private var INSTANCE: ThinkUpDatabase? = null
        
        fun getDatabase(context: Context): ThinkUpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ThinkUpDatabase::class.java,
                    "thinkup_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
