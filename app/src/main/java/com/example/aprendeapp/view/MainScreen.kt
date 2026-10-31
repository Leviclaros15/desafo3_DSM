package com.example.aprendeapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aprendeapp.controller.ResourceController

@Composable
fun MainScreen(controller: ResourceController, onNavigateToTeacherPanel: () -> Unit, isTeacher: Boolean) {
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        controller.fetchResources()
    }

    val filteredResources = controller.resourceList.filter {
        it.titulo.contains(searchQuery, ignoreCase = true) ||
                it.tipo.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Recursos de Ingeniería", style = MaterialTheme.typography.headlineMedium)
            if (isTeacher) {
                Button(onClick = onNavigateToTeacherPanel) {
                    Text("Panel Docente")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar por título o tipo...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (controller.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredResources) { resource ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = resource.titulo, style = MaterialTheme.typography.titleLarge)
                            Text(text = "Tipo: ${resource.tipo}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = resource.descripcion, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}