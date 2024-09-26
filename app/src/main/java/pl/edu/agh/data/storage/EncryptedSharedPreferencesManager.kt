package pl.edu.agh.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import pl.edu.agh.data.remote.dto.LoginResponse
import pl.edu.agh.data.remote.dto.RefreshTokenResponse
import pl.edu.agh.model.UserRole

object EncryptedSharedPreferencesManager {

    private const val PREFS_FILENAME = "secure_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val REFRESH_TOKEN_KEY = "refresh_token"
    private const val USER_ID_KEY = "user_id"
    private const val USER_ROLE_KEY = "user_role"
    private const val COMPANY_ID_KEY = "company_id"

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREFS_FILENAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    fun saveLoggedInUser(loginResponse: LoginResponse) {
        saveAccessToken(loginResponse.accessToken)
        saveRefreshToken(loginResponse.refreshToken)
        saveUserId(loginResponse.userId)
        saveUserRole(loginResponse.userRole)
        saveCompanyId(loginResponse.companyId)
    }

    fun saveRefreshedTokens(refreshTokenResponse: RefreshTokenResponse) {
        saveAccessToken(refreshTokenResponse.accessToken)
        saveRefreshToken(refreshTokenResponse.refreshToken)
    }

    private fun saveAccessToken(token: String) {
        saveProperty(ACCESS_TOKEN_KEY, token)
    }

    private fun saveRefreshToken(token: String) {
        saveProperty(REFRESH_TOKEN_KEY, token)
    }

    private fun saveUserId(userId: Int) {
        saveProperty(USER_ID_KEY, userId)
    }

    private fun saveUserRole(userRole: UserRole) {
        saveProperty(USER_ROLE_KEY, userRole.name)
    }

    private fun saveCompanyId(companyId: Int) {
        saveProperty(COMPANY_ID_KEY, companyId)
    }

    fun getAccessToken(): String {
        return getStringProperty(ACCESS_TOKEN_KEY)
    }

    fun getRefreshToken(): String {
        return getStringProperty(REFRESH_TOKEN_KEY)
    }

    fun getCompanyId(): Int {
        return getIntProperty(COMPANY_ID_KEY)
    }

    fun getUserId(): Int {
        return getIntProperty(USER_ID_KEY)
    }

    fun getUserRole(): UserRole {
        val roleName = getStringProperty(USER_ROLE_KEY)
        return UserRole.valueOf(roleName)
    }

    fun clearUserData() {
        checkInitialized()
        sharedPreferences.edit().clear().apply()
    }

    private fun <T> saveProperty(key: String, value: T) {
        checkInitialized()
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                else -> putString(key, value.toString())
            }
            apply()
        }
    }

    private fun getStringProperty(key: String): String {
        checkInitialized()
        return sharedPreferences.getString(key, null)
            ?: throw IllegalStateException("Property not found: $key")
    }

    private fun getIntProperty(key: String): Int {
        checkInitialized()
        return sharedPreferences.getInt(key, Int.MIN_VALUE)
    }

    private fun checkInitialized() {
        if (!::sharedPreferences.isInitialized) {
            throw IllegalStateException("EncryptedSharedPreferencesManager is not initialized")
        }
    }
}