package org.example.onlinestoreapi.strategies

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import org.example.onlinestoreapi.entities.Product
import java.time.Clock

class ExpirationDiscountStrategy(val expirationLimit: Long) : DiscountStrategy {

    private var clock: Clock = Clock.systemUTC()

    constructor(
        expirationLimit: Long,
        clock: Clock
    ) : this(expirationLimit) {
        this.clock = clock
    }

    override fun applyDiscount(product: Product, discount: Int): Product {
        val currentDate = LocalDate.now(clock)
        val daysUntilExpiration = ChronoUnit.DAYS.between(currentDate, product.expirationDate)

        return if (daysUntilExpiration in 0..expirationLimit) {
            product.copy(price = product.price * (100f - discount) / 100)
        } else {
            product
        }
    }
}
