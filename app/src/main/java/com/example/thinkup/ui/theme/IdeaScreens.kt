package com.example.thinkup.ui

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thinkup.model.Idea
import com.example.thinkup.viewmodel.IdeaViewModel
import com.example.thinkup.viewmodel.IdeasState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// pestañas: Mapa, Formulario y Mis ideas
enum class IdeasTab { MAP, ADD, MINE }

/* ================== Contenedor principal ================== */
@Composable
fun IdeasHome(
    authName: String,                 // nombre del usuario logueado
    onBackToHome: () -> Unit,
    ideaVM: IdeaViewModel = viewModel()
) {
    val st by ideaVM.state.collectAsState()
    var tab by rememberSaveable { mutableStateOf(IdeasTab.MAP) }

    when (tab) {
        IdeasTab.MAP -> IdeasMapFullScreen(
            st = st,
            onAddIdea = { tab = IdeasTab.ADD },
            onRandom = { ideaVM.randomCommunityIdea() },  // comunidad
            onMine = { tab = IdeasTab.MINE },
            onBack = onBackToHome
        )
        IdeasTab.ADD -> AddIdeaForm(
            onSave = { t, d, c ->
                ideaVM.saveIdea(t, d, c, authName)
                tab = IdeasTab.MAP
            },
            onMarkerInfo = { lat, lng -> ideaVM.setMarker(lat, lng) },
            selectedLat = st.selectedLat,
            selectedLng = st.selectedLng
        )
        IdeasTab.MINE -> MyIdeasScreen(
            authName = authName,
            st = st,
            onBack = { tab = IdeasTab.MAP }
        )
    }
}

/* ================== Mapa FULL SCREEN + barra abajo ================== */
@Composable
fun IdeasMapFullScreen(
    st: IdeasState,
    onAddIdea: () -> Unit,
    onRandom: () -> Unit,
    onMine: () -> Unit,
    onBack: () -> Unit
) {
    val camPos = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-33.4489, -70.6693), 11f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = camPos
        ) {
            val all = st.items + sampleIdeasForChile()
            all.forEach { idea ->
                Marker(
                    state = MarkerState(LatLng(idea.lat, idea.lng)),
                    title = idea.title,
                    snippet = "${idea.category} • ${idea.author}"
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
                .fillMaxWidth(0.95f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            st.error?.let { AssistChip(onClick = {}, label = { Text(it) }) }
            st.randomIdea?.let {
                ElevatedCard {
                    Column(Modifier.padding(12.dp)) {
                        Text("Idea random", style = MaterialTheme.typography.titleMedium)
                        Text(it.title, style = MaterialTheme.typography.titleLarge)
                        if (it.description.isNotBlank()) Text(it.description)
                        Text("Categoría: ${it.category}")
                        Text("Autor: ${it.author}")
                        TextButton(onClick = onRandom) { Text("Otra") }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(12.dp)
                .fillMaxWidth(),
            tonalElevation = 6.dp,
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAddIdea,
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text("💡 Agregar") }

                OutlinedButton(
                    onClick = onRandom,
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text("🎲 Random") }

                OutlinedButton(
                    onClick = onMine,
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text("🧑 Mis ideas") }

                TextButton(
                    onClick = onBack,
                    modifier = Modifier.height(52.dp)
                ) { Text("↩️ Volver") }
            }
        }
    }
}

/* ================== MIS IDEAS + perfil ================== */
@Composable
fun MyIdeasScreen(
    authName: String,
    st: IdeasState,
    onBack: () -> Unit
) {
    val mine = remember(st.items, authName) {
        st.items.filter { it.author.equals(authName, ignoreCase = true) }
            .sortedByDescending { it.id }
    }

    Box(Modifier.fillMaxSize().systemBarsPadding(), contentAlignment = Alignment.Center) {
        ElevatedCard(Modifier.fillMaxWidth(0.92f)) {
            Column(
                Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header de usuario
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserAvatar(initials = initialsFromName(authName))
                    Column {
                        Text(authName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Tus ideas publicadas: ${mine.size}")
                    }
                }

                Divider()

                if (mine.isEmpty()) {
                    Text("Aún no has agregado ideas. ¡Publica la primera! 😊")
                } else {
                    mine.forEach { idea ->
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(idea.title, style = MaterialTheme.typography.titleMedium)
                                if (idea.description.isNotBlank()) Text(idea.description)
                                Text("Categoría: ${idea.category}")
                                Text("Ubicación: %.4f, %.4f".format(idea.lat, idea.lng))
                            }
                        }
                    }
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onBack) { Text("Volver al mapa") }
                }
            }
        }
    }
}

@Composable
private fun UserAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun initialsFromName(name: String): String =
    name.trim()
        .split(Regex("\\s+"))
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")

/* ================== Formulario ================== */
@SuppressLint("MissingPermission")
@Composable
fun AddIdeaForm(
    onSave: (String, String, String) -> Unit,
    onMarkerInfo: (Double, Double) -> Unit,
    selectedLat: Double?,
    selectedLng: Double?
) {
    val ctx = LocalContext.current
    val fused = remember { LocationServices.getFusedLocationProviderClient(ctx) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("") }
    val scroll = rememberScrollState()

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ElevatedCard(Modifier.fillMaxWidth(0.92f)) {
            Column(
                Modifier.padding(20.dp).verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("💡 Nueva Idea", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(title, { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(desc, { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(cat, { cat = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())

                Text("Selecciona una ubicación en el mapa:")
                Box(
                    modifier = Modifier.fillMaxWidth().height(240.dp).clip(MaterialTheme.shapes.medium)
                ) {
                    SelectableMap(
                        onPick = { lat, lng -> onMarkerInfo(lat, lng) },
                        selectedLat = selectedLat,
                        selectedLng = selectedLng
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (selectedLat != null && selectedLng != null) {
                        Text("📍 %.4f, %.4f".format(selectedLat, selectedLng),
                            color = MaterialTheme.colorScheme.primary)
                    } else {
                        Text("Toca el mapa o usa tu ubicación")
                    }
                    TextButton(onClick = {
                        fused.lastLocation.addOnSuccessListener { loc: Location? ->
                            loc?.let { onMarkerInfo(it.latitude, it.longitude) }
                        }
                    }) { Text("Usar mi ubicación") }
                }

                Button(
                    onClick = { onSave(title.trim(), desc.trim(), cat.trim()) },
                    enabled = title.isNotBlank() && desc.isNotBlank() && cat.isNotBlank()
                            && selectedLat != null && selectedLng != null,
                    modifier = Modifier.fillMaxWidth(0.7f).height(52.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text("Guardar") }
            }
        }
    }
}

/* ================== Mapa de selección ================== */
@Composable
fun SelectableMap(
    onPick: (Double, Double) -> Unit,
    selectedLat: Double?,
    selectedLng: Double?
) {
    val start = LatLng(-33.4489, -70.6693)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(start, 12f)
    }
    GoogleMap(
        cameraPositionState = cameraState,
        properties = MapProperties(),
        uiSettings = MapUiSettings(zoomControlsEnabled = true),
        onMapClick = { p -> onPick(p.latitude, p.longitude) },
        modifier = Modifier.fillMaxSize()
    ) {
        if (selectedLat != null && selectedLng != null) {
            Marker(state = MarkerState(LatLng(selectedLat, selectedLng)), title = "Idea aquí")
        }
    }
}

/* ================== Ideas “comunidad” para poblar el mapa ================== */
private fun sampleIdeasForChile(): List<Idea> = listOf(
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
