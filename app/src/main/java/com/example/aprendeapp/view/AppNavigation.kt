package com.example.aprendeapp.view

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aprendeapp.controller.ResourceController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val resourceController = remember { ResourceController() }

    // Variable para simular o almacenar el rol actual del usuario autenticado
    var userRole by remember { mutableStateOf("Estudiante") } // O "Docente"

    NavHost(navController = navController, startDestination = "login") {

        // 1. Pantalla de Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { email ->
                    // Redirigir según el rol del usuario
                    val destination = if (userRole == "Docente") "teacher_panel" else "main_screen"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true } // Elimina el login del historial
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // 2. Pantalla de Registro
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    // Volver al login tras un registro exitoso
                    navController.popBackStack()
                }
            )
        }

        // 3. Pantalla Principal (Vista Estudiante)
        composable("main_screen") {
            MainScreen(
                controller = resourceController,
                onNavigateToTeacherPanel = {
                    navController.navigate("teacher_panel")
                },
                isTeacher = (userRole == "Docente")
            )
        }

        // 4. Panel Docente (CRUD Completo)
        composable("teacher_panel") {
            TeacherDashboardScreen(
                controller = resourceController
            )
        }
    }
}