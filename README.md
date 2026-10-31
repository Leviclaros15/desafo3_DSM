# AprendeApp 📱🎓

**AprendeApp** es una aplicación móvil desarrollada en **Android** utilizando **Jetpack Compose** y **Kotlin**, diseñada para facilitar la gestión y consulta de recursos educativos. Permite una interacción fluida tanto para estudiantes como para docentes mediante roles de usuario, consumo de una API REST, almacenamiento local y características interactivas avanzadas.

---

## 🚀 Características Principales

* **Autenticación y Roles de Usuario**: 
  * Registro e inicio de sesión seguro con persistencia local (`SharedPreferences`).
  * Control de acceso basado en roles (**Estudiante** y **Docente**).
* **Consumo de API REST (CRUD Completo)**:
  * Integración con **Retrofit** para consumir y gestionar recursos educativos de forma remota en tiempo real.
  * Operaciones de Creación (POST), Lectura (GET), Actualización (PUT) y Eliminación (DELETE) desde el Panel Docente.
* **Sistema de Favoritos**:
  * Permite a los estudiantes marcar y desmarcar recursos como favoritos, con filtrado rápido en la interfaz.
* **Sistema de Calificación por Estrellas (Único por Usuario)**:
  * Calificación interactiva del 1 al 5 estrellas.
  * Restricción por usuario para evitar spam de votos, permitiendo actualizar su puntuación.
  * Cálculo dinámico y visualización del promedio global de cada recurso (ej. `4.5 (3)`).
* **Multimedia y Enlaces Externos**:
  * Carga de imágenes optimizada mediante **Coil** (`AsyncImage`) con placeholders automáticos de carga y error.
  * Tarjetas interactivas que abren el enlace del recurso (PDF, video, artículos) directamente en el navegador web del dispositivo.
* **Búsqueda y Filtrado**:
  * Barra de búsqueda instantánea por título o tipo de recurso.

---

## Tecnologías y Librerías Utilizadas

* **Lenguaje**: Kotlin
* **Interfaz de Usuario**: Jetpack Compose (Material Design 3)
* **Arquitectura**: Consumo de API REST con Retrofit y Gson
* **Persistencia Local**: SharedPreferences (Gestión de sesiones, favoritos y calificaciones)
* **Carga de Imágenes**: Coil Compose (`AsyncImage`)
* **Navegación / UI Components**: Material 3 `ModalNavigationDrawer`, `LazyColumn`, `Scaffold`

---

## Estructura del Proyecto

* `com.example.aprendeapp.model`: Modelos de datos (`Resource`, `User`) y cliente HTTP (`ApiClient`).
* `com.example.aprendeapp.controller`: Lógica de negocio y controladores de estado (`ResourceController`).
* `com.example.aprendeapp.view`: Pantallas de la aplicación en Jetpack Compose (`LoginScreen`, `RegisterScreen`, `MainScreen`, `TeacherPanelScreen`).

