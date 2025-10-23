package com.example.thinkup.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.thinkup.model.Idea
import com.example.thinkup.model.User

@Database(
    entities = [User::class, Idea::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
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
