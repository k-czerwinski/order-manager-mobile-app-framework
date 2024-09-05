package pl.edu.agh.model

enum class UserRole(val urlName: String) {
    CLIENT("client"),
    COURIER("courier"),
    ADMIN("admin") // not allowed in this app
}
