package pl.edu.agh.framework.model

interface UserRoleParserInterface {
    fun values() : List<UserRoleInterface>
    fun valueOf(name: String): UserRoleInterface
    fun isValueSupported(name: String): Boolean
}

object UserRoleDependencyInjector {
    private var userRoleParserInterface: UserRoleParserInterface? = null

    fun registerUserRoleParser(userRoleParserInterface: UserRoleParserInterface) {
        if (UserRoleDependencyInjector.userRoleParserInterface != null) {
            throw IllegalStateException("UserRoleParser is already registered")
        }
        UserRoleDependencyInjector.userRoleParserInterface = userRoleParserInterface
    }

    fun getUserRoleParser(): UserRoleParserInterface {
        return userRoleParserInterface ?: throw IllegalStateException("UserRoleParser is not registered")
    }
}