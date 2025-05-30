package com.example.newnoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.newnoteapp.domain.FileNotebook
import com.example.newnoteapp.ui.theme.NewnoteappTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private lateinit var notebook: FileNotebook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notebook = FileNotebook.load(this)

        Timber.plant(Timber.DebugTree())
        enableEdgeToEdge()

        setContent {
            NewnoteappTheme {
                TestOperationsUI(this, notebook)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NewnoteappTheme {
        Greeting("Android")
    }
}