package com.example.aprendeapp.model

data class Resource(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val enlace: String,
    val imagen: String,
    val calificacionPromedio: Double? = 0.0
)

fun validatePassword(password: String): Boolean {
    val minLength = password.length >= 12
    val hasUpper = password.any { it.isUpperCase() }
    val hasLower = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }

    return minLength && hasUpper && hasLower && hasDigit && hasSpecial
}