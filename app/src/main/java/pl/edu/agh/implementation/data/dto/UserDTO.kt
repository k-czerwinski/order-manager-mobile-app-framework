package pl.edu.agh.implementation.data.dto

import kotlinx.serialization.Serializable
import pl.edu.agh.framework.model.User
import pl.edu.agh.framework.model.UserListViewItem
import pl.edu.agh.framework.model.UserRoleDependencyInjector

@Serializable
data class UserDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val role: String,
    val companyId: Int
) {
    companion object {
        private val userRoleParser = UserRoleDependencyInjector.getUserRoleParser()
        fun toModel(userDTO: UserDTO): User {
            return User(
                id = userDTO.id,
                firstName = userDTO.firstName,
                lastName = userDTO.lastName,
                username = userDTO.username,
                role = userRoleParser.valueOf(userDTO.role)
            )
        }
    }
}

@Serializable
data class UserCreateDTO(
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String,
    val role: String
)

@Serializable
data class UserListViewItemDTO(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val role: String,
) {
    companion object {
        private val userRoleParser = UserRoleDependencyInjector.getUserRoleParser()
        fun toModel(userListViewItemDTO: UserListViewItemDTO): UserListViewItem {
            return UserListViewItem(
                id = userListViewItemDTO.id,
                firstName = userListViewItemDTO.firstName,
                lastName = userListViewItemDTO.lastName,
                role = userRoleParser.valueOf(userListViewItemDTO.role),
            )
        }
    }
}