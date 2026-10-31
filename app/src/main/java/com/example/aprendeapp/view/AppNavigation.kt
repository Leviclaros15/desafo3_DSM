package com.example.aprendeapp.view

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aprendeapp.controller.ResourceController
import com.example.aprendeapp.controller.UserController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val resourceController = remember { ResourceController() }
    val userController = remember { UserController(context) }

    var currentUserRole by remember { mutableStateOf("Estudiante") }

    NavHost(navController = navController, startDestination = "login") {

        // 1. Pantalla de Login con verificación real
        composable("login") {
            LoginScreen(
                onLoginSuccess = { email, password ->
                    val role = userController.loginUser(email, password)
                    if (role != null) {
                        currentUserRole = role
                        Toast.makeText(context, "¡Bienvenido! Rol: $role", Toast.LENGTH_SHORT).show()
                        val destination = if (role == "Docente") "teacher_panel" else "main_screen"
                        navController.navigate(destination) {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // 2. Pantalla de Registro con guardado real
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { email, password, role ->
                    val success = userController.registerUser(email, password, role)
                    if (success) {
                        Toast.makeText(context, "Registro exitoso. Inicia sesión.", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "El correo ya está registrado.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // 3. Pantalla Principal (Estudiante)
        composable("main_screen") {
            MainScreen(
                controller = resourceController,
                onNavigateToTeacherPanel = {
                    navController.navigate("teacher_panel")
                },
                isTeacher = (currentUserRole == "Docente")
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