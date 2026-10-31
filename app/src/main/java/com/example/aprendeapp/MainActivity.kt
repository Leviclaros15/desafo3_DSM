package com.example.aprendeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.aprendeapp.ui.theme.AprendeAppTheme
import com.example.aprendeapp.view.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AprendeAppTheme {
                AppNavigation()
            }
        }
    }
}