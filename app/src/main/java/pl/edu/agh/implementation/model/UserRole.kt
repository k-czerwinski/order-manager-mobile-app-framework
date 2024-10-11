package pl.edu.agh.implementation.model

import pl.edu.agh.framework.model.UserRoleInterface
import pl.edu.agh.framework.model.UserRoleParser
import pl.edu.agh.implementation.presentation.navigation.CustomNavigation

enum class UserRole(override val urlName: String, override val navigation: CustomNavigation) : UserRoleInterface {
    CLIENT("client", CustomNavigation.Client),
    COURIER("courier", CustomNavigation.Courier),
    ADMIN("admin", CustomNavigation.Admin);
}

object UserRoleParserImpl: UserRoleParser {
    override fun values(): List<UserRoleInterface> {
        return UserRole.values().toList()
    }

    override fun valueOf(name: String): UserRoleInterface {
        return UserRole.valueOf(name)
    }
}
