package pl.edu.agh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.framework.presentation.navigation.AppNavigation
import pl.edu.agh.implementation.model.UserRoleParserImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        UserRoleDependencyInjector.registerUserRoleParser(UserRoleParserImpl)
        EncryptedSharedPreferencesManager.initialize(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}
