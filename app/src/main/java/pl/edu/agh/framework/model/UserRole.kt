package pl.edu.agh.framework.model

import pl.edu.agh.implementation.presentation.navigation.CustomNavigation

enum class UserRole(
    val urlName: String,
    val navigation: CustomNavigation
) {
    CLIENT("client", CustomNavigation.Client),
    COURIER("courier", CustomNavigation.Courier),
    ADMIN("admin", CustomNavigation.Admin)
}
