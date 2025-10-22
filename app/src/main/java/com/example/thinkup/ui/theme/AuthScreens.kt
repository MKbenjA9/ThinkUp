package com.example.thinkup.ui.theme


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import com.example.thinkup.viewmodel.AuthViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thinkup.ui.IdeasHome
import com.example.thinkup.viewmodel.Screen

@Composable
fun AuthApp(vm: AuthViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    when (state.current) {
        Screen.Login -> LoginScreen(state.error, vm::login, { vm.goTo(Screen.Register) })
        Screen.Register -> RegisterScreen(state.error, vm::register, { vm.goTo(Screen.Login) })
        Screen.Home -> HomeScreen(state.user?.name ?: "Usuario", vm::logout)
        Screen.Home -> TODO()
        Screen.Login -> TODO()
        Screen.Register -> TODO()
    }
}

@Composable
fun LoginScreen(error: String?, onLogin: (String, String) -> Unit, goRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    AuthCard("Iniciar Sesión", error) {
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

        Button(onClick = { onLogin(email, pass) }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar")
        }
        TextButton(onClick = goRegister) { Text("Crear cuenta") }
    }
}

@Composable
fun RegisterScreen(error: String?, onRegister: (String, String, String, String) -> Unit, goLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    AuthCard("Registrar", error) {
        OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(confirm, { confirm = it }, label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

        Button(onClick = { onRegister(name, email, pass, confirm) }, modifier = Modifier.fillMaxWidth()) {
            Text("Crear cuenta")
        }
        TextButton(onClick = goLogin) { Text("Ya tengo cuenta") }
    }
}


@Composable
fun HomeScreen(name: String, onLogout: () -> Unit) {
    var showIdeas by remember { mutableStateOf(false) }

    if (showIdeas) {
        IdeasHome(
            authName = name,
            onBackToHome = { showIdeas = false }
        )
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("¡Hola, $name!", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { showIdeas = true }) { Text("Explorar / Proponer ideas") }
                Button(onClick = onLogout) { Text("Cerrar sesión") }
            }
        }
    }
}


@Composable
private fun AuthCard(title: String, error: String?, content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ElevatedCard(Modifier.padding(16.dp).fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(title, style = MaterialTheme.typography.headlineSmall)
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
                content()
            }
        }
    }
}
