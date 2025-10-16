package com.example.thinkup.ui.theme


import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.thinkup.viewmodel.IdeaViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thinkup.viewmodel.IdeasState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun IdeasHome(
    authName: String,
    onBackToHome: () -> Unit,
    ideaVM: IdeaViewModel = viewModel()
) {
    val st by ideaVM.state.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(true) } // mapa por defecto

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showMap = true; showAdd = false }) { Text("Ver mapa") }
            Button(onClick = { showMap = false; showAdd = true }) { Text("Agregar idea") }
            Button(onClick = { ideaVM.randomIdea() }) { Text("Idea random") }
            OutlinedButton(onClick = onBackToHome) { Text("Volver") }
        }

        if (st.error != null) Text(st.error!!, color = MaterialTheme.colorScheme.error)

        if (st.randomIdea != null) {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Idea random", style = MaterialTheme.typography.titleMedium)
                    Text(st.randomIdea!!.title, style = MaterialTheme.typography.titleLarge)
                    Text(st.randomIdea!!.description)
                    Text("Categoría: ${st.randomIdea!!.category}")
                    Text("Autor: ${st.randomIdea!!.author}")
                    TextButton(onClick = { ideaVM.clearRandom() }) { Text("Cerrar") }
                }
            }
        }

        if (showAdd) {
            AddIdeaForm(
                onSave = { t, d, c -> ideaVM.saveIdea(t, d, c, authName) },
                onMarkerInfo = { lat, lng -> ideaVM.setMarker(lat, lng) },
                selectedLat = st.selectedLat,
                selectedLng = st.selectedLng
            )
        } else if (showMap) {
            IdeasMap(st)
        }

        Text("Total de ideas: ${st.items.size}")
    }
}

@Composable
fun AddIdeaForm(
    onSave: (String, String, String) -> Unit,
    onMarkerInfo: (Double, Double) -> Unit,
    selectedLat: Double?,
    selectedLng: Double?
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("") }

    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Nueva Idea", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(title, { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(desc, { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(cat, { cat = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())

            Text("Elige ubicación en el mapa:")
            Box(Modifier.height(250.dp).fillMaxWidth()) {
                SelectableMap(onPick = { lat, lng -> onMarkerInfo(lat, lng) }, selectedLat = selectedLat, selectedLng = selectedLng)
            }

            Text(if (selectedLat != null && selectedLng != null) "Ubicación: $selectedLat, $selectedLng" else "Sin ubicación seleccionada")

            Button(onClick = { onSave(title, desc, cat) }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar idea")
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun SelectableMap(
    onPick: (Double, Double) -> Unit,
    selectedLat: Double?,
    selectedLng: Double?
) {
    val ctx = LocalContext.current
    val fused = remember { LocationServices.getFusedLocationProviderClient(ctx) }
    var camPos by remember { mutableStateOf(CameraPositionState()) }

    var hasFine by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasFine = granted
    }
    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }


    val defaultPos = com.google.android.gms.maps.model.LatLng(-33.4489, -70.6693)

    LaunchedEffect(hasFine) {
        if (hasFine) {
            fused.lastLocation.addOnSuccessListener { loc: Location? ->
                val here = if (loc != null) com.google.android.gms.maps.model.LatLng(loc.latitude, loc.longitude) else defaultPos
                camPos.move(CameraUpdateFactory.newLatLngZoom(here, 13f))
            }
        } else {
            camPos.move(CameraUpdateFactory.newLatLngZoom(defaultPos, 12f))
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = camPos,
        properties = MapProperties(isMyLocationEnabled = hasFine),
        onMapClick = { latLng ->
            onPick(latLng.latitude, latLng.longitude)
        }
    ) {
        if (selectedLat != null && selectedLng != null) {
            Marker(
                state = MarkerState(position = com.google.android.gms.maps.model.LatLng(selectedLat, selectedLng)),
                title = "Idea aquí"
            )
        }
    }
}

@Composable
fun IdeasMap(st: IdeasState) {
    val camPos = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(-33.4489, -70.6693), // Santiago centro
            11f
        )
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        cameraPositionState = camPos
    ) {
        st.items.forEach { idea ->
            Marker(
                state = MarkerState(position = LatLng(idea.lat, idea.lng)),
                title = idea.title,
                snippet = "${idea.category} • ${idea.author}"
            )
        }
    }
}

