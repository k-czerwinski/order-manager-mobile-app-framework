package pl.edu.agh

fun setPrivateField(obj: Any, fieldName: String, value: Any) {
    val property = obj::class.java.declaredFields.find { it.name == fieldName }
        ?: throw IllegalArgumentException("No such property $fieldName found")
    property.isAccessible = true
    property.set(obj, value)
}