package com.example.newnoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.newnoteapp.ui.navigate.NoteNavigation
import com.example.newnoteapp.ui.theme.NewnoteappTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewnoteappTheme {
                NoteNavigation()
            }
        }
    }
}
