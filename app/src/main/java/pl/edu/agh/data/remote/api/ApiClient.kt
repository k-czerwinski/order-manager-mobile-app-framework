package pl.edu.agh.data.remote.api

import android.net.http.X509TrustManagerExtensions
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String, val companyId: Int)
object ApiClient {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun login() {
        val response = client.post("https://10.0.2.2:8443/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("username", "password", 1))
        }
        Log.e("LoginResponse", response.body())
        Log.e("LoginResponseStatus", response.status.toString())
    }

}