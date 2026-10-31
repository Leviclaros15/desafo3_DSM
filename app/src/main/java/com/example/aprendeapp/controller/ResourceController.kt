package com.example.aprendeapp.controller

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.aprendeapp.model.ApiClient
import com.example.aprendeapp.model.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResourceController(context: Context? = null) {
    var resourceList by mutableStateOf<List<Resource>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // --- Gestión de Favoritos ---
    private val prefs: SharedPreferences? = context?.getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE)
    var favoriteIds = mutableStateOf<Set<String>>(emptySet())
        private set

    // --- Gestión de Calificaciones y Promedios Únicas por Usuario ---
    private val ratingPrefs: SharedPreferences? = context?.getSharedPreferences("RatingsPrefs", Context.MODE_PRIVATE)

    // Estado simple para forzar actualización visual en la UI
    var ratingVersion by mutableStateOf(0)
        private set

    init {
        prefs?.let {
            val savedFavs = it.getStringSet("fav_ids", emptySet()) ?: emptySet()
            favoriteIds.value = savedFavs
        }
    }

    fun toggleFavorite(resourceId: String) {
        val currentFavs = favoriteIds.value.toMutableSet()
        if (currentFavs.contains(resourceId)) {
            currentFavs.remove(resourceId)
        } else {
            currentFavs.add(resourceId)
        }
        favoriteIds.value = currentFavs
        prefs?.edit()?.putStringSet("fav_ids", currentFavs)?.apply()
    }

    fun isFavorite(resourceId: String): Boolean {
        return favoriteIds.value.contains(resourceId)
    }

    fun rateResource(resourceId: String, rating: Int, userEmail: String) {
        if (userEmail.isBlank()) return

        val userRatingKey = "rating_${resourceId}_$userEmail"
        val votesKey = "votes_$resourceId"
        val sumKey = "sum_$resourceId"

        val existingRating = ratingPrefs?.getInt(userRatingKey, -1) ?: -1
        val currentVotes = ratingPrefs?.getInt(votesKey, 0) ?: 0
        val currentSum = ratingPrefs?.getInt(sumKey, 0) ?: 0

        val newVotes: Int
        val newSum: Int

        if (existingRating == -1) {
            // Primera vez que este usuario califica
            newVotes = currentVotes + 1
            newSum = currentSum + rating
        } else {
            // El usuario ya había votado, actualizamos su nota sin inflar los votos totales
            newVotes = currentVotes
            newSum = currentSum - existingRating + rating
        }

        // Guardamos la calificación individual y los acumuladores globales
        ratingPrefs?.edit()
            ?.putInt(userRatingKey, rating)
            ?.putInt(votesKey, newVotes)
            ?.putInt(sumKey, newSum)
            ?.apply()

        // Incrementamos la versión para redibujar al instante
        ratingVersion++
    }

    fun getRating(resourceId: String, userEmail: String): Int {
        val trigger = ratingVersion
        if (userEmail.isBlank()) return 0
        return ratingPrefs?.getInt("rating_${resourceId}_$userEmail", 0) ?: 0
    }

    // Método para obtener el promedio y el total de votos globales
    fun getResourceAverage(resourceId: String): Pair<Double, Int> {
        val trigger = ratingVersion
        val votes = ratingPrefs?.getInt("votes_$resourceId", 0) ?: 0
        val sum = ratingPrefs?.getInt("sum_$resourceId", 0) ?: 0
        val avg = if (votes > 0) sum.toDouble() / votes else 0.0
        return Pair(avg, votes)
    }

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