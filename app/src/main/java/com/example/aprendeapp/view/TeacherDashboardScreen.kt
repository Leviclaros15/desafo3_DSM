package com.example.aprendeapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aprendeapp.controller.ResourceController
import com.example.aprendeapp.model.Resource

@Composable
fun TeacherDashboardScreen(controller: ResourceController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        controller.fetchResources()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Panel Docente - Gestión CRUD", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipo (Libro, Video, etc.)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = link, onValueChange = { link = it }, label = { Text("Enlace URL") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = image, onValueChange = { image = it }, label = { Text("Imagen URL") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val newResource = Resource(id = editingId ?: (controller.resourceList.size + 1), titulo = title, descripcion = description, tipo = type, enlace = link, imagen = image)
            if (editingId == null) {
                controller.addResource(newResource) {
                    title = ""; description = ""; type = ""; link = ""; image = ""
                }
            } else {
                controller.updateResource(editingId!!, newResource) {
                    editingId = null; title = ""; description = ""; type = ""; link = ""; image = ""
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (editingId == null) "Agregar Recurso (POST)" else "Actualizar Recurso (PUT)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(controller.resourceList) { resource ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = resource.titulo, style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                editingId = resource.id
                                title = resource.titulo
                                description = resource.descripcion
                                type = resource.tipo
                                link = resource.enlace
                                image = resource.imagen
                            }) {
                                Text("Editar")
                            }
                            Button(colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), onClick = {
                                controller.deleteResource(resource.id)
                            }) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}