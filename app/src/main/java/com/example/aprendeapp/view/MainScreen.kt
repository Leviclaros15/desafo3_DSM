package com.example.aprendeapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aprendeapp.controller.ResourceController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    controller: ResourceController,
    userEmail: String,
    onNavigateToTeacherPanel: () -> Unit,
    onLogout: () -> Unit,
    isTeacher: Boolean
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    // Estado para saber si estamos mostrando solo los favoritos
    var showOnlyFavorites by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        controller.fetchResources()
    }

    // 1. Filtrar por búsqueda de texto
    val filteredResources = controller.resourceList.filter {
        it.titulo.contains(searchQuery, ignoreCase = true) ||
                it.tipo.contains(searchQuery, ignoreCase = true)
    }

    // 2. Filtrar adicionalmente si el usuario activó "Solo Favoritos"
    val displayedResources = if (showOnlyFavorites) {
        filteredResources.filter { controller.isFavorite(it.id.toString()) }
    } else {
        filteredResources
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Usuario",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Usuario Conectado", style = MaterialTheme.typography.titleMedium)
                    Text(text = userEmail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión") },
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = if (showOnlyFavorites) "Mis Favoritos" else "AprendeApp - Recursos") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
                        }
                    },
                    actions = {
                        // Botón en la barra superior para alternar entre Todos y Favoritos
                        IconButton(onClick = { showOnlyFavorites = !showOnlyFavorites }) {
                            Icon(
                                imageVector = if (showOnlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Filtrar Favoritos",
                                tint = if (showOnlyFavorites) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (isTeacher) {
                            TextButton(onClick = onNavigateToTeacherPanel) {
                                Text("Panel Docente")
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar por título o tipo...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (controller.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (displayedResources.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = if (showOnlyFavorites) "No tienes recursos favoritos aún." else "No se encontraron recursos.")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(displayedResources) { resource ->
                            val resourceIdStr = resource.id.toString()
                            val isFav = controller.isFavorite(resourceIdStr)
                            val userRating = controller.getRating(resourceIdStr)

                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = resource.titulo, style = MaterialTheme.typography.titleLarge)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = "Tipo: ${resource.tipo}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = resource.descripcion, style = MaterialTheme.typography.bodySmall)
                                        }

                                        // Botón de Corazón para marcar/desmarcar favorito
                                        IconButton(onClick = {
                                            controller.toggleFavorite(resourceIdStr)
                                        }) {
                                            Icon(
                                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "Marcar favorito",
                                                tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Sistema de calificación por estrellas corregido
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Califica:", style = MaterialTheme.typography.bodyMedium)

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            for (i in 1..5) {
                                                IconButton(
                                                    onClick = { controller.rateResource(resourceIdStr, i) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = "Estrella $i",
                                                        tint = if (i <= userRating) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}