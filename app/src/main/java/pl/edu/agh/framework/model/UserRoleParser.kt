package pl.edu.agh.framework.model

interface UserRoleParser {
    fun values() : List<UserRoleInterface>
    fun valueOf(name: String): UserRoleInterface
}

object UserRoleDependencyInjector {
    private var userRoleParser: UserRoleParser? = null

    fun registerUserRoleParser(userRoleParser: UserRoleParser) {
        if (UserRoleDependencyInjector.userRoleParser != null) {
            throw IllegalStateException("UserRoleParser is already registered")
        }
        UserRoleDependencyInjector.userRoleParser = userRoleParser
    }

    fun getUserRoleParser(): UserRoleParser {
        return userRoleParser ?: throw IllegalStateException("UserRoleParser is not registered")
    }
}