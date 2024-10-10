package pl.edu.agh.framework.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import pl.edu.agh.BuildConfig
import pl.edu.agh.framework.data.remote.dto.LoginRequest
import pl.edu.agh.framework.data.remote.dto.LoginResponse
import pl.edu.agh.framework.data.remote.dto.LogoutRequest
import pl.edu.agh.framework.data.remote.dto.RefreshTokenRequest
import pl.edu.agh.framework.data.remote.dto.RefreshTokenResponse
import pl.edu.agh.framework.data.storage.EncryptedSharedPreferencesManager

class HttpResponseException(val httpStatusCode: HttpStatusCode, message: String) :
    RuntimeException(message)

object ApiClient {
    internal val SERVER_URL = "https://${BuildConfig.SERVER_HOST}:${BuildConfig.SERVER_PORT}"
    private val LOGIN_URL = SERVER_URL + BuildConfig.LOGIN_PATH
    private val REFRESH_TOKEN_URL = SERVER_URL + BuildConfig.REFRESH_TOKEN_PATH
    private val LOGOUT_URL = SERVER_URL + BuildConfig.LOGOUT_PATH

    internal val anonymousClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
    }
    internal val authenticatedClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(
                        accessToken = EncryptedSharedPreferencesManager.getAccessToken(),
                        refreshToken = EncryptedSharedPreferencesManager.getRefreshToken()
                    )
                }
                refreshTokens {
                    val oldRefreshToken = EncryptedSharedPreferencesManager.getRefreshToken()

                    val refreshTokenResponse =
                        refreshToken(oldRefreshToken)
                    EncryptedSharedPreferencesManager.saveRefreshedTokens(refreshTokenResponse)
                    BearerTokens(
                        accessToken = refreshTokenResponse.accessToken,
                        refreshToken = refreshTokenResponse.refreshToken
                    )
                }
            }
        }
    }

    suspend fun ApiClient.login(loginRequest: LoginRequest): LoginResponse {
        val response = anonymousClient.post(LOGIN_URL) {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }
        Log.d("ApiClient", "Login response: $response")
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Unauthorized -> throw HttpResponseException(
                response.status,
                "Invalid credentials provided for login"
            )

            else -> throw HttpResponseException(
                response.status,
                "Unexpected error during login"
            )
        }
    }

    private suspend fun ApiClient.refreshToken(refreshToken: String): RefreshTokenResponse {
        val response = anonymousClient.post(REFRESH_TOKEN_URL) {
            setBody(RefreshTokenRequest(refreshToken))
            contentType(ContentType.Application.Json)
        }
        Log.d("ApiClient", "Refresh token response: $response")
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw HttpResponseException(
                response.status,
                "Invalid or expired refresh token or unexpected error"
            )
        }

    }

    suspend fun ApiClient.logout(refreshToken: String) {
        val response = anonymousClient.post(LOGOUT_URL) {
            setBody(LogoutRequest(refreshToken))
            contentType(ContentType.Application.Json)
        }
        Log.d("ApiClient", response.toString())
    }
}
