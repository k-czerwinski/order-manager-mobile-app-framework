package pl.edu.agh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.presentation.ui.theme.OrderManagerTheme
import pl.edu.agh.implementation.presentation.navigation.AppNavigation

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