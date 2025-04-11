package org.example.onlinestoreapi.entities

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.UUID

@Entity
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?,
    val name: String?,
    @Enumerated(EnumType.STRING)
    val category: ProductCategory?,
    val expirationDate: LocalDate?,
    val price: Double
) {
    constructor() : this(null, null, null, null, -1.0)
}
