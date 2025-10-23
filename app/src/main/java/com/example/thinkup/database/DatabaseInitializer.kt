package com.example.thinkup.database

import android.content.Context
import com.example.thinkup.model.Idea
import com.example.thinkup.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class to initialize the database with sample data
 */
class DatabaseInitializer(private val context: Context) {
    
    private val database = ThinkUpDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val ideaDao = database.ideaDao()
    
    /**
     * Initialize database with sample data
     */
    suspend fun initializeSampleData() = withContext(Dispatchers.IO) {
        try {
            // Create demo user
            val demoUser = User(
                email = "demo@thinkup.com",
                name = "Usuario Demo",
                password = "demo123"
            )
            
            // Insert demo user if not exists
            if (userDao.getUserByEmail(demoUser.email) == null) {
                userDao.insertUser(demoUser)
            }
            
            // Create sample ideas
            val sampleIdeas = listOf(
                Idea(
                    title = "App de Recetas Saludables",
                    description = "Una aplicación que sugiere recetas basadas en ingredientes disponibles y preferencias dietéticas.",
                    category = "Salud",
                    lat = 40.4168,
                    lng = -3.7038,
                    author = demoUser.email
                ),
                Idea(
                    title = "Sistema de Gestión de Inventario",
                    description = "Software para pequeñas empresas que necesitan controlar su inventario de manera eficiente.",
                    category = "Negocios",
                    lat = 40.4200,
                    lng = -3.7100,
                    author = demoUser.email
                ),
                Idea(
                    title = "Plataforma de Aprendizaje Online",
                    description = "Una plataforma educativa que conecta estudiantes con tutores especializados.",
                    category = "Educación",
                    lat = 40.4100,
                    lng = -3.7000,
                    author = demoUser.email
                ),
                Idea(
                    title = "Completo italiano",
                    description = "Un completo italiano tradicional con palta y tomate.",
                    category = "Comida",
                    lat = -33.4569,
                    lng = -70.6483,
                    author = demoUser.email
                ),
                Idea(
                    title = "Cerro San Cristóbal",
                    description = "Un paseo por el cerro más emblemático de Santiago.",
                    category = "Paseo",
                    lat = -33.4275,
                    lng = -70.6335,
                    author = demoUser.email
                ),
                Idea(
                    title = "Barrio Lastarria",
                    description = "Un barrio cultural con cafés y librerías.",
                    category = "Visita",
                    lat = -33.4387,
                    lng = -70.6426,
                    author = demoUser.email
                )
            )
            
            // Insert sample ideas
            sampleIdeas.forEach { idea ->
                ideaDao.insertIdea(idea)
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Clear all data from database
     */
    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        try {
            ideaDao.deleteAllIdeas()
            userDao.deleteAllUsers()
            true
        } catch (e: Exception) {
            false
        }
    }
}
