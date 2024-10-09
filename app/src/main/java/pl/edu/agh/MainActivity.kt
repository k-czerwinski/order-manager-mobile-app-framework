package pl.edu.agh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.presentation.navigation.AppNavigation
import pl.edu.agh.presentation.ui.theme.OrderManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EncryptedSharedPreferencesManager.initialize(this)
        enableEdgeToEdge()
        setContent {
            OrderManagerTheme {
                AppNavigation()
            }
        }
    }
}