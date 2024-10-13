package pl.edu.agh.framework.model

import pl.edu.agh.framework.presentation.navigation.CustomNavigationInterface

interface UserRoleInterface {
    val urlName: String
    val displayNameCode: Int
    val navigation: CustomNavigationInterface
}