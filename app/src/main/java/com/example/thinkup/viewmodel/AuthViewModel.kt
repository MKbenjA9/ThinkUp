package com.example.thinkup.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkup.repository.UserRepository
import com.example.thinkup.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class Screen { object Login : Screen(); object Register : Screen(); object Home : Screen() }

data class UiState(
    val current: Screen = Screen.Login,
    val user: User? = null,
    val error: String? = null
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = UserRepository(app.applicationContext)
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun goTo(screen: Screen) {
        _state.value = _state.value.copy(current = screen, error = null)
    }

    fun register(name: String, email: String, pass: String, confirm: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || confirm.isBlank()) {
            _state.value = _state.value.copy(error = "Completa todos los campos.")
            return
        }
        if (pass != confirm) {
            _state.value = _state.value.copy(error = "Las contraseñas no coinciden.")
            return
        }

        viewModelScope.launch {
            val ok = repo.register(User(name, email, pass))
            _state.value = if (ok)
                UiState(current = Screen.Login)
            else
                _state.value.copy(error = "Ya hay un usuario registrado.")
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _state.value = _state.value.copy(error = "Completa email y contraseña.")
            return
        }

        viewModelScope.launch {
            val user = repo.login(email, pass)
            _state.value = if (user != null)
                UiState(current = Screen.Home, user = user)
            else
                _state.value.copy(error = "Credenciales incorrectas.")
        }
    }

    fun logout() {
        repo.logout()
        _state.value = UiState(current = Screen.Login)
    }
}
