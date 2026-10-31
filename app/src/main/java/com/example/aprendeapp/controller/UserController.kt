package com.example.aprendeapp.controller

import android.content.Context
import android.content.SharedPreferences

class UserController(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AprendeAppPrefs", Context.MODE_PRIVATE)

    // Registrar un nuevo usuario
    fun registerUser(email: String, pass: String, role: String): Boolean {
        // Verificamos si ya existe el usuario comprobando si su llave de contraseña ya fue guardada
        if (prefs.contains("${email}_pass")) {
            return false // El usuario ya existe
        }

        // Guardamos las credenciales usando commit() para asegurar la persistencia inmediata en disco
        return prefs.edit()
            .putString("${email}_pass", pass)
            .putString("${email}_role", role)
            .commit()
    }

    // Verificar credenciales en el Login
    fun loginUser(email: String, pass: String): String? {
        val savedPass = prefs.getString("${email}_pass", null)
        if (savedPass != null && savedPass == pass) {
            // Retorna el rol guardado del usuario
            return prefs.getString("${email}_role", "Estudiante")
        }
        return null // Credenciales inválidas o usuario no encontrado
    }
}