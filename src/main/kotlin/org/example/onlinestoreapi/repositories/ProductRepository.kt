package org.example.onlinestoreapi.repositories

import java.util.UUID
import org.example.onlinestoreapi.entities.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, UUID> {}
