package com.example.aprendeapp.controller

import android.content.Context
import android.content.SharedPreferences

class UserController(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AprendeAppPrefs", Context.MODE_PRIVATE)

    // Registrar un nuevo usuario
    fun registerUser(email: String, pass: String, role: String): Boolean {
        if (prefs.contains(email)) {
            return false // El usuario ya existe
        }
        // Guardamos la contraseña y el rol usando el email como clave
        prefs.edit().putString("${email}_pass", pass)
            .putString("${email}_role", role)
            .apply()
        return true
    }

    // Verificar credenciales en el Login
    fun loginUser(email: String, pass: String): String? {
        val savedPass = prefs.getString("${email}_pass", null)
        if (savedPass != null && savedPass == pass) {
            // Retorna el rol del usuario si las credenciales son correctas
            return prefs.getString("${email}_role", "Estudiante")
        }
        return null // Credenciales inválidas
    }
}