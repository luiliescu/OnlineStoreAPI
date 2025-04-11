package org.example.onlinestoreapi.e2e

import io.mockk.every
import org.example.onlinestoreapi.entities.Product
import org.example.onlinestoreapi.entities.ProductCategory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun contextLoads() {}

    @Test
    fun `GET on root path should return 200`() {
        val response = restTemplate.getForEntity("http://localhost:$port/", String::class.java)
        assertTrue(response.statusCode.is2xxSuccessful)
        assertTrue(response.body!!.contains("Let's buy some products!"))
    }

    @Test
    fun `GET products should return an empty list if db is empty`() {
        val response = restTemplate.getForEntity("http://localhost:$port/products", List::class.java)
        assertTrue(response.statusCode.is2xxSuccessful)
        assertTrue(response.body!!.isEmpty())
    }

    @Test
    fun `POST products should add an item to the db`() {

        val postResponse = restTemplate.postForEntity(
            "http://localhost:$port/products",
            mapOf(
                "name" to "name",
                "category" to "MEAT",
                "expirationDate" to "2021-01-01",
                "price" to 100.0
            ),
            String::class.java)
        assertTrue(postResponse.statusCode.is2xxSuccessful)

        val expectedProduct = Product(
            UUID.randomUUID(),
            "name",
            ProductCategory.MEAT,
            LocalDate.ofInstant(Instant.parse("2021-01-01T00:00:00Z"), Clock.systemUTC().zone),
            100.01
        )

        val getResponse = restTemplate.getForEntity("http://localhost:$port/products", List::class.java)
        assertTrue(getResponse.statusCode.is2xxSuccessful)
        assertEquals(1, getResponse.body!!.size)
        println(getResponse.body)
    }
}
