package org.example.onlinestoreapi.unit

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.example.onlinestoreapi.entities.Product
import org.example.onlinestoreapi.repositories.ProductRepository
import org.example.onlinestoreapi.services.ProductService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import java.time.LocalDate
import java.util.Optional
import java.util.UUID
import org.example.onlinestoreapi.entities.ProductCategory
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProductServiceTests {

    val productRepository = mockk<ProductRepository>()

    val productsList: MutableList<Product> = mutableListOf()

    @InjectMockKs
    lateinit var productService: ProductService

    @BeforeEach
    fun setup() {
        productsList.clear()

        every { productRepository.findAll() }  answers  {
            productsList
        }

        every { productRepository.save(any(Product::class)) } answers {
            val product: Product = firstArg<Product>()
            productsList.removeAll { it.id == product.id }
            productsList.add(product)
            product
        }

        every { productRepository.findById(any(UUID::class)) } answers  {
            val uuid: UUID = firstArg<UUID>()
            val product = productsList.find { it.id == uuid }
            Optional.ofNullable(product)
        }

        every { productRepository.deleteById(any(UUID::class)) } answers {
            val uuid: UUID = firstArg<UUID>()
            productsList.removeAll { it.id == uuid }
        }
    }

    @Test
    fun `product service should return empty list if the db is empty`() {
        assertTrue(productService.getAllProducts().isEmpty())
    }

    @Test
    fun `add product should add an entry to the db`() {
        val product = Product(UUID.randomUUID(), "name1", ProductCategory.NONE, LocalDate.now(), 10.0)
        val expectedSize = 1
        productService.addProduct(product)

        val products = productService.getAllProducts()
        assertEquals(expectedSize, products.size)
        assertEquals(product, products.first())
    }

    @Test
    fun `get product by id should return the product with that id`() {
        val uuid = UUID.randomUUID()
        val expectedProduct = Product(uuid, "name1", ProductCategory.NONE, LocalDate.now(), 10.0)
        productService.addProduct(Product())
        productService.addProduct(expectedProduct)

        assertEquals(expectedProduct, productService.getProductById(uuid))
    }

    @Test
    fun `get product by id should return null if the product does not exist`() {
        assertEquals(null, productService.getProductById(UUID.randomUUID()))
    }

    @Test
    fun `delete product by id should remove the product from db`() {
        val uuid = UUID.randomUUID()
        val product = Product(uuid, "name1", ProductCategory.NONE, LocalDate.now(), 10.0)
        val initialSize = 1

        productService.addProduct(product)
        assertEquals(initialSize, productsList.size)
        productService.deleteProductById(uuid)
        assertTrue(productsList.isEmpty())
    }

    @Test
    fun `update product should update existing product`() {
        val uuid = UUID.randomUUID()
        val expectedProduct = Product(uuid, "name1", ProductCategory.NONE, LocalDate.now(), 10.0)

        productService.addProduct(expectedProduct)
        val updatedProduct = expectedProduct.copy(name = "cheese", category = ProductCategory.CHEESE, expirationDate = LocalDate.now(), price = 20.0)
        productService.updateProduct(uuid, updatedProduct)
        assertEquals(updatedProduct, productService.getProductById(uuid))
    }

    @Test
    fun `update product should not update if product does not exist`() {
        val uuid = UUID.randomUUID()
        val expectedProduct = Product(uuid, "name1", ProductCategory.NONE, LocalDate.now(), 10.0)

        productService.addProduct(expectedProduct)
        val updatedProduct = expectedProduct.copy(name = "cheese", category = ProductCategory.CHEESE, expirationDate = LocalDate.now(), price = 20.0)
        productService.updateProduct(UUID.randomUUID(), updatedProduct)
        assertEquals(expectedProduct, productService.getProductById(uuid))
    }

}
