package com.example.thinkup.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkup.repository.IdeasRepository
import com.example.thinkup.model.Idea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

data class IdeasState(
    val items: List<Idea> = emptyList(),
    val selectedLat: Double? = null,
    val selectedLng: Double? = null,
    val error: String? = null,
    val randomIdea: Idea? = null
)

class IdeaViewModel(app: Application): AndroidViewModel(app) {
    private val repo = IdeasRepository(app.applicationContext)
    private val _state = MutableStateFlow(IdeasState())
    val state: StateFlow<IdeasState> = _state.asStateFlow()

    init { 
        observeIdeas()
    }

    private fun observeIdeas() {
        viewModelScope.launch {
            repo.getAll().collect { ideas ->
                _state.value = _state.value.copy(items = ideas, error = null)
            }
        }
    }

    fun refresh() {
        // The Flow will automatically update the state when data changes
        _state.value = _state.value.copy(error = null)
    }

    fun setMarker(lat: Double, lng: Double) {
        _state.value = _state.value.copy(selectedLat = lat, selectedLng = lng)
    }

    fun saveIdea(title: String, description: String, category: String, author: String) {
        val lat = _state.value.selectedLat
        val lng = _state.value.selectedLng
        if (title.isBlank() || description.isBlank() || category.isBlank() || lat == null || lng == null) {
            _state.value = _state.value.copy(error = "Completa todos los campos y elige una ubicación en el mapa.")
            return
        }
        viewModelScope.launch {
            try {
                val idea = Idea(
                    title = title.trim(),
                    description = description.trim(),
                    category = category.trim(),
                    lat = lat,
                    lng = lng,
                    author = author
                )
                val ideaId = repo.saveIdea(idea)
                if (ideaId > 0) {
                    _state.value = _state.value.copy(
                        selectedLat = null,
                        selectedLng = null,
                        error = null
                    )
                } else {
                    _state.value = _state.value.copy(error = "Error al guardar la idea.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al guardar la idea: ${e.message}")
            }
        }
    }


    fun deleteIdea(id: Long) {
        viewModelScope.launch {
            try {
                val success = repo.deleteIdea(id)
                if (!success) {
                    _state.value = _state.value.copy(error = "Error al eliminar la idea.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al eliminar la idea: ${e.message}")
            }
        }
    }


    private val communityPool = listOf(
        Idea(100, "Completo italiano", "", "Comida", -33.4569, -70.6483, "Cristian"),
        Idea(101, "Cerro San Cristóbal", "", "Paseo", -33.4275, -70.6335, "María"),
        Idea(102, "Barrio Lastarria", "", "Visita", -33.4387, -70.6426, "Josefa"),
        Idea(103, "Pomaire", "Empanadas y artesanía", "Comida", -33.5561, -71.1778, "Valentina"),
        Idea(104, "Parque Quinta Normal", "Museos y áreas verdes", "Paseo", -33.4430, -70.6837, "Sebastián"),
        Idea(105, "Viña del Mar", "Playas y Muelle Vergara", "Visita", -33.0245, -71.5518, "Carolina"),
        Idea(106, "Cerro Ñielol (Temuco)", "Bosque nativo", "Paseo", -38.7259, -72.5975, "Felipe"),
        Idea(107, "Mercado Central", "Mariscos", "Comida", -33.4331, -70.6476, "Catalina"),
        Idea(108, "Plaza de Armas Valdivia", "Río y ferias", "Visita", -39.8142, -73.2459, "Ignacio"),
        Idea(109, "San Pedro de Atacama", "Paisajes únicos", "Paseo", -22.9087, -68.1997, "Andrea"),
        Idea(110, "Curanto en Chiloé", "Tradición chilota", "Comida", -42.4796, -73.7622, "Pablo"),
        Idea(111, "Lago Llanquihue", "Vista al Osorno", "Visita", -41.3160, -72.9854, "Sofía"),
        Idea(112, "Cajón del Maipo", "Trekking y naturaleza", "Paseo", -33.6552, -70.3273, "Benjamín")
    )

    fun randomCommunityIdea() {
        _state.value = _state.value.copy(randomIdea = communityPool.random(), error = null)
    }

    fun randomMyIdea() {
        viewModelScope.launch {
            try {
                val randomIdea = repo.getRandom()
                if (randomIdea != null) {
                    _state.value = _state.value.copy(randomIdea = randomIdea, error = null)
                } else {
                    _state.value = _state.value.copy(error = "Aún no tienes ideas guardadas.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al obtener idea aleatoria: ${e.message}")
            }
        }
    }

    fun randomMixed() {
        viewModelScope.launch {
            try {
                val randomIdea = repo.getRandom()
                val pool = if (randomIdea != null) communityPool + randomIdea else communityPool
                _state.value = _state.value.copy(randomIdea = pool.random(), error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error al obtener idea aleatoria: ${e.message}")
            }
        }
    }

    fun clearRandom() {
        _state.value = _state.value.copy(randomIdea = null)
    }
}
