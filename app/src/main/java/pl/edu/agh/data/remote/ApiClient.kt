package pl.edu.agh.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import pl.edu.agh.data.remote.dto.LoginRequest
import pl.edu.agh.data.remote.dto.LoginResponse

class HttpResponseException(val httpStatusCode: HttpStatusCode, message: String) : RuntimeException(message)

object ApiClient {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun login(loginRequest: LoginRequest) : LoginResponse {
        val response = client.post("https://10.0.2.2:8443/login") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }
        Log.d("ApiClient", "Login response: $response")
        return when(response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Unauthorized -> throw HttpResponseException(response.status, "Invalid credentials provided for login")
            else -> throw HttpResponseException(response.status, "Unexpected error during login")
        }
    }

}