package com.example.thinkup.viewmodel




import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkup.repository.IdeasRepository
import com.example.thinkup.model.Idea
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

class IdeaViewModel(app: Application): AndroidViewModel(app) {
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
            repo.saveIdea(Idea(id, title.trim(), description.trim(), category.trim(), lat, lng, author))
            _state.value = _state.value.copy(
                items = repo.getAll(),
                selectedLat = null,
                selectedLng = null,
                error = null
            )
        }
    }

    fun randomIdea() {
        _state.value = _state.value.copy(randomIdea = repo.getRandom())
    }

    fun clearRandom() {
        _state.value = _state.value.copy(randomIdea = null)
    }
}
