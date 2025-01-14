package pl.edu.agh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.framework.model.UserRoleDependencyInjector
import pl.edu.agh.framework.presentation.navigation.AppNavigation
import pl.edu.agh.framework.presentation.ui.theme.AppTheme
import pl.edu.agh.implementation.model.UserRoleParserInterfaceImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        UserRoleDependencyInjector.registerUserRoleParser(UserRoleParserInterfaceImpl)
        EncryptedSharedPreferencesManager.initialize(this)
        ApiClient.initialize()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigation()
            }
        }
    }
}
