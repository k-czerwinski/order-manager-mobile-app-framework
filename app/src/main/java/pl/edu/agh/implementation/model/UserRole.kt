package pl.edu.agh.implementation.model

import io.ktor.util.toUpperCasePreservingASCIIRules
import pl.edu.agh.R
import pl.edu.agh.framework.model.UserRoleInterface
import pl.edu.agh.framework.model.UserRoleParserInterface
import pl.edu.agh.implementation.presentation.navigation.CustomNavigation

enum class UserRole(
    override val urlName: String,
    override val displayNameCode: Int,
    override val navigation: CustomNavigation
) : UserRoleInterface {
    ADMIN("admin", R.string.admin_role_display_name, CustomNavigation.Admin),
    CLIENT("client", R.string.client_role_display_name, CustomNavigation.Client),
    COURIER("courier", R.string.courier_role_display_name, CustomNavigation.Courier)
}

object UserRoleParserInterfaceImpl : UserRoleParserInterface {
    override fun values(): List<UserRoleInterface> = UserRole.entries

    override fun valueOf(name: String): UserRoleInterface = UserRole.valueOf(name.toUpperCasePreservingASCIIRules())

    override fun isValueSupported(name: String): Boolean =
        UserRole.entries.toList().map(UserRole::toString).any { it.lowercase() == name.lowercase() }
}
