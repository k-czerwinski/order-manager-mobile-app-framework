package pl.edu.agh.implementation.data.dto

import kotlinx.serialization.Serializable
import pl.edu.agh.framework.model.Company

@Serializable
data class CompanyDTO(
    val id: Int,
    val name: String,
    val domain: String
) {
    companion object {
        fun toModel(dto: CompanyDTO): Company {
            return Company(dto.id, dto.name, dto.domain)
        }
    }
}