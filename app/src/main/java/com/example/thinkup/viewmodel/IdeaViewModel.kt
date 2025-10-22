package com.example.thinkup.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkup.model.Idea
import com.example.thinkup.repository.IdeasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

data class IdeasState(
    val items: List<Idea> = emptyList(),
    val selectedLat: Double? = null,
    val selectedLng: Double? = null,
    val error: String? = null,
    val randomIdea: Idea? = null
)

class IdeaViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = IdeasRepository(app)
    private val _state = MutableStateFlow(IdeasState())
    val state: StateFlow<IdeasState> = _state

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(items = repo.getAll(), error = null)
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
            val id = System.currentTimeMillis() + abs(title.hashCode())
            repo.saveIdea(
                Idea(id, title.trim(), description.trim(), category.trim(), lat, lng, author)
            )
            _state.value = _state.value.copy(
                items = repo.getAll(),
                selectedLat = null,
                selectedLng = null,
                error = null
            )
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
        if (communityPool.isEmpty()) {
            _state.value = _state.value.copy(error = "No hay ideas de la comunidad aún 😅")
            return
        }
        _state.value = _state.value.copy(randomIdea = communityPool.random(), error = null)
    }


    fun randomMyIdea() {
        val mine = repo.getAll()
        if (mine.isEmpty()) {
            _state.value = _state.value.copy(error = "Aún no tienes ideas guardadas.")
            return
        }
        _state.value = _state.value.copy(randomIdea = mine.random(), error = null)
    }


    fun randomMixed() {
        val pool = repo.getAll() + communityPool
        _state.value = _state.value.copy(randomIdea = pool.random(), error = null)
    }


    fun randomIdea() {
        _state.value = _state.value.copy(randomIdea = repo.getRandom())
    }

    fun clearRandom() {
        _state.value = _state.value.copy(randomIdea = null)
    }
}
