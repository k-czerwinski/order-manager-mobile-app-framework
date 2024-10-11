package pl.edu.agh.framework.model

import java.math.BigDecimal

data class Product(
    val id: Int,
    val name: String,
    val price: BigDecimal,
    val description: String
)