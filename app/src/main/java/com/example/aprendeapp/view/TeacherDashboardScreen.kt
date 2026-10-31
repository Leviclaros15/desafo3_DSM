package com.example.aprendeapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.aprendeapp.controller.ResourceController
import com.example.aprendeapp.model.Resource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    controller: ResourceController,
    userEmail: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        controller.fetchResources()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                // Cabecera del menú lateral con icono y correo del docente
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Docente",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Panel Docente",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                // Opción para Cerrar Sesión
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
                    title = { Text(text = "Gestión CRUD Docente") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sección del Formulario
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("Tipo (Libro, Video, etc.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = link,
                        onValueChange = { link = it },
                        label = { Text("Enlace URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = image,
                        onValueChange = { image = it },
                        label = { Text("Imagen URL") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && description.isNotBlank() && type.isNotBlank() && link.isNotBlank() && image.isNotBlank()) {
                                val newResource = Resource(
                                    id = editingId ?: (controller.resourceList.size + 1),
                                    titulo = title,
                                    descripcion = description,
                                    tipo = type,
                                    enlace = link,
                                    imagen = image
                                )
                                if (editingId == null) {
                                    controller.addResource(newResource) {
                                        title = ""; description = ""; type = ""; link = ""; image = ""
                                        Toast.makeText(context, "Recurso agregado correctamente", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    controller.updateResource(editingId!!, newResource) {
                                        editingId = null; title = ""; description = ""; type = ""; link = ""; image = ""
                                        Toast.makeText(context, "Recurso actualizado correctamente", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (editingId == null) "Agregar Recurso (POST)" else "Actualizar Recurso (PUT)")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lista de Recursos en la Nube",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Lista de recursos
                items(controller.resourceList) { resource ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = resource.titulo, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Tipo: ${resource.tipo}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = resource.descripcion, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(onClick = {
                                    editingId = resource.id
                                    title = resource.titulo
                                    description = resource.descripcion
                                    type = resource.tipo
                                    link = resource.enlace
                                    image = resource.imagen
                                }) {
                                    Text("Editar")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    onClick = {
                                        controller.deleteResource(resource.id)
                                        Toast.makeText(context, "Recurso eliminado", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}