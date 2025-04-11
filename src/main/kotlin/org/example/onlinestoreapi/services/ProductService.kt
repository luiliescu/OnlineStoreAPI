package org.example.onlinestoreapi.services

import java.util.UUID
import org.example.onlinestoreapi.entities.Product
import org.example.onlinestoreapi.repositories.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductService(
    @Autowired
    private val repository: ProductRepository
) {

    fun getAllProducts(): List<Product>  {
        return repository.findAll()
    }

    fun addProduct(product: Product): Product {
        return repository.save(product)
    }

    fun getProductById(id: UUID): Product? {
        return repository.findById(id).orElse(null)
    }

    fun deleteProductById(id: UUID) {
        return repository.deleteById(id)
    }

    fun updateProduct(id: UUID, product: Product): Product? {
        val existingProduct = repository.findById(id).orElse(null)
        if (existingProduct != null) {
            val updatedProduct = existingProduct.copy(
                name = product.name,
                category = product.category,
                expirationDate = product.expirationDate,
                price = product.price
            )
            return repository.save(updatedProduct)
        }
        return null
    }
}
