package com.example.aprendeapp.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.aprendeapp.model.ApiClient
import com.example.aprendeapp.model.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResourceController {
    var resourceList by mutableStateOf<List<Resource>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchResources() {
        CoroutineScope(Dispatchers.IO).launch {
            isLoading = true
            errorMessage = null
            try {
                val response = ApiClient.apiService.getResources()
                if (response.isSuccessful) {
                    resourceList = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error al cargar recursos: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Excepción de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addResource(resource: Resource, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.addResource(resource)
                if (response.isSuccessful) {
                    fetchResources()
                    withContext(Dispatchers.Main) { onSuccess() }
                }
            } catch (e: Exception) {
                // Manejar error de red
            }
        }
    }

    fun updateResource(id: Int, resource: Resource, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.updateResource(id, resource)
                if (response.isSuccessful) {
                    fetchResources()
                    withContext(Dispatchers.Main) { onSuccess() }
                }
            } catch (e: Exception) {
                // Manejar error de red
            }
        }
    }

    fun deleteResource(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.deleteResource(id)
                if (response.isSuccessful) {
                    fetchResources()
                }
            } catch (e: Exception) {
                // Manejar error de red
            }
        }
    }
}