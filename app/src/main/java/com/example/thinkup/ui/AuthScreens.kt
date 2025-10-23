package com.example.thinkup.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thinkup.R
import com.example.thinkup.viewmodel.AuthViewModel
import com.example.thinkup.viewmodel.Screen

@Composable
fun AuthApp(vm: AuthViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    when (state.current) {
        Screen.Login -> LoginScreen(
            error = state.error,
            onLogin = vm::login,
            goRegister = { vm.goTo(Screen.Register) }
        )
        Screen.Register -> RegisterScreen(
            error = state.error,
            onRegister = vm::register,
            goLogin = { vm.goTo(Screen.Login) }
        )
        Screen.Home -> HomeScreen(
            name = state.user?.name ?: "Usuario",
            onLogout = vm::logout
        )
    }
}

@Composable
fun LoginScreen(
    error: String?,
    onLogin: (String, String) -> Unit,
    goRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    AuthCard(title = "Iniciar Sesión", error = error) {

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.logo_thinkup),
            contentDescription = "Logo ThinkUp",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = pass, onValueChange = { pass = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(onClick = { onLogin(email, pass) }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar")
        }
        TextButton(onClick = goRegister) { Text("Crear cuenta") }
    }
}

@Composable
fun RegisterScreen(
    error: String?,
    onRegister: (String, String, String, String) -> Unit,
    goLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    AuthCard(title = "Registrar", error = error) {

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.logo_thinkup),
            contentDescription = "Logo ThinkUp",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = pass, onValueChange = { pass = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = confirm, onValueChange = { confirm = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = { onRegister(name, email, pass, confirm) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear cuenta") }

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

                Image(
                    painter = painterResource(id = R.drawable.logo_thinkup),
                    contentDescription = "Logo ThinkUp",
                    modifier = Modifier.size(96.dp)
                )
                Text("¡Hola, $name!", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { showIdeas = true }) { Text("Explorar / Proponer ideas") }
                Button(onClick = onLogout) { Text("Cerrar sesión") }
            }
        }
    }
}

@Composable
private fun AuthCard(
    title: String,
    error: String?,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, style = MaterialTheme.typography.headlineSmall)
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
                content()
            }
        }
    }
}
