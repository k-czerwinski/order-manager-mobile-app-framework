package pl.edu.agh.implementation.data

import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager

fun EncryptedSharedPreferencesManager.saveUserName(userName: String) {
    saveCustomProperty("user_name", userName)
}

fun EncryptedSharedPreferencesManager.getUserName(): String {
    return getCustomProperty("user_name")
}