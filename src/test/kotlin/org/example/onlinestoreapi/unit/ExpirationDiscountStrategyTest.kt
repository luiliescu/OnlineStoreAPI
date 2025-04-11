package org.example.onlinestoreapi.unit

import org.example.onlinestoreapi.entities.Product
import org.example.onlinestoreapi.entities.ProductCategory
import org.example.onlinestoreapi.strategies.ExpirationDiscountStrategy
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals

class ExpirationDiscountStrategyTest {

    val clock = Clock.fixed(Instant.parse("2021-01-01T00:00:00Z"), Clock.systemUTC().zone)

    @Test
    fun `should apply discount to products expire in expirationLimit days`() {
        val discount = 10
        val product = Product(
            UUID.randomUUID(),
            "name",
            ProductCategory.MEAT,
            LocalDate.ofInstant(Instant.parse("2021-01-06T00:00:00Z"), clock.zone),
            100.0
        )

        val strategy = ExpirationDiscountStrategy(5, clock)
        val expectedProduct = product.copy(price = product.price * (100f - discount) / 100)
        assertEquals(expectedProduct, strategy.applyDiscount(product, discount))
    }

    @Test
    fun `should apply discount to products which expire today`() {
        val discount = 10
        val product = Product(
            UUID.randomUUID(),
            "name",
            ProductCategory.MEAT,
            LocalDate.ofInstant(Instant.parse("2021-01-01T00:00:00Z"), clock.zone),
            100.0
        )

        val strategy = ExpirationDiscountStrategy(5, clock)
        val expectedProduct = product.copy(price = product.price * (100f - discount) / 100)
        assertEquals(expectedProduct, strategy.applyDiscount(product, discount))
    }

    @Test
    fun `should not apply discount to products which don't expire soon`() {
        val discount = 10
        val product = Product(
            UUID.randomUUID(),
            "name",
            ProductCategory.MEAT,
            LocalDate.ofInstant(Instant.parse("2021-01-07T00:00:00Z"), clock.zone),
            100.0
        )

        val strategy = ExpirationDiscountStrategy(5, clock)
        assertEquals(product, strategy.applyDiscount(product, discount))
    }

    @Test
    fun `should not apply discount to products which are expired`() {
        val discount = 10
        val product = Product(
            UUID.randomUUID(),
            "name",
            ProductCategory.MEAT,
            LocalDate.ofInstant(Instant.parse("2020-12-31T00:00:00Z"), clock.zone),
            100.0
        )

        val strategy = ExpirationDiscountStrategy(5, clock)
        assertEquals(product, strategy.applyDiscount(product, discount))
    }
}