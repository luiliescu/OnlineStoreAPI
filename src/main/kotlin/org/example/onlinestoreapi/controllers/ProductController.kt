package org.example.onlinestoreapi.controllers

import org.example.onlinestoreapi.entities.Product
import org.example.onlinestoreapi.entities.ProductCategory
import org.example.onlinestoreapi.services.ProductService
import org.example.onlinestoreapi.strategies.DiscountStrategy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*

@RestController
class ProductsController(
    private val productService: ProductService,
    @Autowired
    private val discountStrategy: DiscountStrategy,
    @Autowired
    private val amount: Int
) {

    private val logger = LoggerFactory.getLogger(ProductsController::class.java)

    @GetMapping("/")
    fun sayHello(): String {
        return "Let's buy some products!"
    }

    @GetMapping("/products")
    fun getProductList(@RequestParam(required = false) discounted: Boolean): List<Product> {
        if (discounted) {
            return productService.getAllProducts().map { discountStrategy.applyDiscount(it, amount) }
        }
        return productService.getAllProducts()
    }

    @GetMapping("/product/{id}")
    fun getProductById(@PathVariable id: UUID): ResponseEntity<Any> {
        val product = productService.getProductById(id)
        if (product == null) {
            logger.error("Product with id $id not found")
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Product not found"))
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(product)
    }

    @GetMapping("/product/{id}/price")
    fun getProductPrice(@PathVariable id: UUID): ResponseEntity<Any> {
        val product = productService.getProductById(id)
        if (product == null) {
            logger.error("Product with id $id not found")
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Product not found"))
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(mapOf("price" to product.price))
    }

    @PostMapping("/products")
    fun addProduct(@RequestBody product: Product): ResponseEntity<Any> {
        if (product.name == null || product.category == null || product.expirationDate == null || product.price < 0) {
            logger.error("Product $product is missing required fields")
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mapOf("message" to "Product is missing required fields"))
        }
        try {
            return ResponseEntity(productService.addProduct(product), HttpStatus.CREATED)
        } catch (e: Exception) {
            logger.error("Error adding product $product: ${e.message}")
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Error adding product"))
        }
    }

    @DeleteMapping("/product/{id}")
    fun deleteProductById(@PathVariable id: UUID): ResponseEntity<Any> {
        val product = productService.getProductById(id)
        if (product == null) {
            logger.error("Product with id $id not found")
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Product not found"))
        }
        try {
            productService.deleteProductById(id)
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapOf("message" to "Product deleted successfully"))
        } catch (e: Exception) {
            logger.error("Error deleting product with id $id: ${e.message}")
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Error deleting product"))
        }

    }

    @PutMapping("/product/{id}")
    fun updateProductById(
        @PathVariable id: UUID,
        @RequestBody productBody: Product
    ): ResponseEntity<Any> {
        val product = productService.getProductById(id)
        if (product == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Product not found"))
        }
        val name = productBody.name ?: product.name
        val category = productBody.category ?: product.category
        val expirationDate = productBody.expirationDate ?: product.expirationDate
        val price = productBody.price.takeIf { it >= 0 } ?: product.price
        val updatedProduct = product.copy(
            name = name,
            category = category,
            expirationDate = expirationDate,
            price = price
        )
        try {
            productService.updateProduct(id, updatedProduct)
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedProduct)
        } catch (e: Exception) {
            logger.error("Error updating product with id $id: ${e.message}")
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Error updating product"))
        }
    }
}
