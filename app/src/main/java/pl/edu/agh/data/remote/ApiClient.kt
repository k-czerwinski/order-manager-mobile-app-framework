package pl.edu.agh.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import pl.edu.agh.BuildConfig
import pl.edu.agh.data.remote.dto.Company
import pl.edu.agh.data.remote.dto.LoginRequest
import pl.edu.agh.data.remote.dto.LoginResponse
import pl.edu.agh.data.remote.dto.OrderCreateDTO
import pl.edu.agh.data.remote.dto.OrderCreateResponseDTO
import pl.edu.agh.data.remote.dto.OrderDTO
import pl.edu.agh.data.remote.dto.OrderListViewItemDTO
import pl.edu.agh.data.remote.dto.ProductDTO
import pl.edu.agh.data.remote.dto.UserDTO
import pl.edu.agh.data.storage.EncryptedSharedPreferencesManager
import pl.edu.agh.model.UserRole

class HttpResponseException(val httpStatusCode: HttpStatusCode, message: String) :
    RuntimeException(message)

object ApiClient {

    private val SERVER_URL = "https://${BuildConfig.SERVER_HOST}:${BuildConfig.SERVER_PORT}"

    private val anonymousClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
    }
    private val authenticatedClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(
                        accessToken = EncryptedSharedPreferencesManager.getAccessToken(),
                        refreshToken = "s"
                    )
                }
            }
        }
    }

    suspend fun login(loginRequest: LoginRequest): LoginResponse {
        val response = anonymousClient.post("${SERVER_URL}/login") {
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

    suspend fun getCompany(companyId: Int) : Company {
        val response = authenticatedClient.get("${SERVER_URL}/company/${companyId}")
        Log.d("ApiClient", response.toString())
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw HttpResponseException(response.status, "Unexpected error fetching company")
        }
    }

    suspend fun getOrders(companyId: Int, userRole: UserRole, userId: Int) : List<OrderListViewItemDTO> {
        val response = authenticatedClient.get("${SERVER_URL}/company/${companyId}/${userRole.urlName}/${userId}/orders")
        Log.d("ApiClient", response.toString())
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw HttpResponseException(response.status, "Unexpected error fetch order list")
        }
    }

    suspend fun getCurrentUser(companyId: Int, userRole: UserRole) : UserDTO {
        val response = authenticatedClient.get("${SERVER_URL}/company/${companyId}/${userRole.urlName}")
        Log.d("ApiClient", response.toString())
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw HttpResponseException(response.status, "Unexpected error fetching user")
        }
    }

    suspend fun getOrderDetails(companyId: Int, userRole: UserRole, userId: Int, orderId: Int) : OrderDTO {
        val response = authenticatedClient.get("${SERVER_URL}/company/${companyId}" +
                "/${userRole.urlName}/${userId}/order/${orderId}")
        Log.d("ApiClient", response.toString())
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw HttpResponseException(response.status, "Unexpected error fetching order details")
        }
    }

    suspend fun getProducts(companyId: Int, userRole: UserRole) : List<ProductDTO> {
        val response = authenticatedClient.get("${SERVER_URL}/company/${companyId}" +
                "/${userRole.urlName}/products")
        Log.d("ApiClient", response.toString())
        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> throw HttpResponseException(response.status, "Unexpected error fetching products")
        }
    }

    suspend fun createOrder(orderCreateDTO: OrderCreateDTO) : OrderCreateResponseDTO {
        val response = authenticatedClient.post("${SERVER_URL}/company/${orderCreateDTO.companyId}" +
                "/client/${orderCreateDTO.clientId}/order") {
            contentType(ContentType.Application.Json)
            setBody(orderCreateDTO)
        }
        Log.d("ApiClient", response.toString())
        return when (response.status) {
            HttpStatusCode.Created -> response.body()
            else -> throw HttpResponseException(response.status, "Unexpected error creating order")
        }
    }
}