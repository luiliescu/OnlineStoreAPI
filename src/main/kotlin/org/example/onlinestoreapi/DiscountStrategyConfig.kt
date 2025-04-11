package org.example.onlinestoreapi

import org.example.onlinestoreapi.configs.DiscountStrategyConfig
import org.example.onlinestoreapi.strategies.CategoryDiscountStrategy
import org.example.onlinestoreapi.strategies.DiscountStrategy
import org.example.onlinestoreapi.strategies.ExpirationDiscountStrategy
import org.example.onlinestoreapi.strategies.StrategyType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.LocalDate

@Configuration
class DiscountStrategyFactory {

    @Autowired
    lateinit var discountStrategyConfig: DiscountStrategyConfig

    @Bean
    fun discountStrategy(): DiscountStrategy {
        return when (discountStrategyConfig.strategy) {
            StrategyType.EXPIRATION -> ExpirationDiscountStrategy(discountStrategyConfig.expirationLimit)
            StrategyType.CATEGORY -> CategoryDiscountStrategy(discountStrategyConfig.discountedCategories)
            else -> throw IllegalArgumentException("No strategy: ${discountStrategyConfig.strategy}")
        }
    }

    @Bean
    fun amount(): Int {
        return discountStrategyConfig.amount
    }

    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }
}
