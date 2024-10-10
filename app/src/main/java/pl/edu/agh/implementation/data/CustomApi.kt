package pl.edu.agh.implementation.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.datetime.LocalDateTime
import pl.edu.agh.framework.data.remote.ApiClient
import pl.edu.agh.framework.data.remote.HttpResponseException
import pl.edu.agh.framework.data.remote.dto.Company
import pl.edu.agh.framework.data.remote.dto.ExpectedDeliveryDateTimeDTO
import pl.edu.agh.framework.data.remote.dto.OrderCreateDTO
import pl.edu.agh.framework.data.remote.dto.OrderCreateResponseDTO
import pl.edu.agh.framework.data.remote.dto.OrderDTO
import pl.edu.agh.framework.data.remote.dto.OrderListViewItemDTO
import pl.edu.agh.framework.data.remote.dto.ProductDTO
import pl.edu.agh.framework.data.remote.dto.UserDTO
import pl.edu.agh.framework.model.UserRole

suspend fun ApiClient.getCompany(companyId: Int): Company {
    val response = authenticatedClient.get("$SERVER_URL/company/${companyId}")
    Log.d("ApiClient", response.toString())
    return when (response.status) {
        HttpStatusCode.OK -> response.body()
        else -> throw HttpResponseException(
            response.status,
            "Unexpected error fetching company"
        )
    }
}

suspend fun ApiClient.getOrders(
    companyId: Int,
    userRole: UserRole,
    userId: Int
): List<OrderListViewItemDTO> {
    val response =
        authenticatedClient.get("$SERVER_URL/company/${companyId}/${userRole.urlName}/${userId}/orders")
    Log.d("ApiClient", response.toString())
    return when (response.status) {
        HttpStatusCode.OK -> response.body()
        else -> throw HttpResponseException(
            response.status,
            "Unexpected error fetch order list"
        )
    }
}

suspend fun ApiClient.getCurrentUser(companyId: Int, userRole: UserRole): UserDTO {
    val response = authenticatedClient.get("$SERVER_URL/company/${companyId}/${userRole.urlName}")
    Log.d("ApiClient", response.toString())
    return when (response.status) {
        HttpStatusCode.OK -> response.body()
        else -> throw HttpResponseException(
            response.status,
            "Unexpected error fetching user"
        )
    }
}

suspend fun ApiClient.getOrderDetails(
    companyId: Int,
    userRole: UserRole,
    userId: Int,
    orderId: Int
): OrderDTO {
    val response = authenticatedClient.get(
        "$SERVER_URL/company/${companyId}" +
                "/${userRole.urlName}/${userId}/order/${orderId}"
    )
    Log.d("ApiClient", response.toString())
    return when (response.status) {
        HttpStatusCode.OK -> response.body()
        else -> throw HttpResponseException(
            response.status,
            "Unexpected error fetching order details"
        )
    }
}

suspend fun ApiClient.getProducts(companyId: Int, userRole: UserRole): List<ProductDTO> {
    val response = authenticatedClient.get(
        "$SERVER_URL/company/${companyId}" +
                "/${userRole.urlName}/products"
    )
    Log.d("ApiClient", response.toString())
    return when (response.status) {
        HttpStatusCode.OK -> response.body()
        else -> throw HttpResponseException(
            response.status,
            "Unexpected error fetching products"
        )
    }
}

suspend fun ApiClient.createOrder(orderCreateDTO: OrderCreateDTO): OrderCreateResponseDTO {
    val response = authenticatedClient.post(
        "$SERVER_URL/company/${orderCreateDTO.companyId}" +
                "/client/${orderCreateDTO.clientId}/order"
    ) {
        contentType(ContentType.Application.Json)
        setBody(orderCreateDTO)
    }
    Log.d("ApiClient", response.toString())
    return when (response.status) {
        HttpStatusCode.Created -> response.body()
        else -> throw HttpResponseException(
            response.status,
            "Unexpected error creating order"
        )
    }
}

suspend fun ApiClient.markOrderAsDelivered(companyId: Int, courierId: Int, orderId: Int) {
    val response = authenticatedClient.put(
        "$SERVER_URL/company/${companyId}" +
                "/courier/${courierId}/order/${orderId}/delivered"
    ) {
        contentType(ContentType.Application.Json)
    }
    Log.d("ApiClient", response.toString())
    if (response.status != HttpStatusCode.NoContent) {
        throw HttpResponseException(
            response.status,
            "Unexpected error when marking order as delivered"
        )
    }
}

suspend fun ApiClient.setExpectedDeliveryDateTime(
    companyId: Int,
    courierId: Int,
    orderId: Int,
    newExpectedDelivery: LocalDateTime
) {
    val response = authenticatedClient.put(
        "$SERVER_URL/company/${companyId}" +
                "/courier/${courierId}/order/${orderId}/expected-delivery"
    ) {
        contentType(ContentType.Application.Json)
        setBody(ExpectedDeliveryDateTimeDTO(newExpectedDelivery))
    }
    Log.d("ApiClient", response.toString())
    if (response.status != HttpStatusCode.NoContent) {
        throw HttpResponseException(
            response.status,
            "Unexpected error when setting expected delivery date"
        )
    }
}