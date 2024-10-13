package pl.edu.agh.framework.model

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String,
    val role: UserRoleInterface,
)

data class UserListViewItem(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val role: UserRoleInterface
)