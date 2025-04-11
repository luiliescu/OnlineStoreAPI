package org.example.onlinestoreapi.configs

import org.example.onlinestoreapi.entities.ProductCategory
import org.example.onlinestoreapi.strategies.StrategyType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "discount")
data class DiscountStrategyConfig(
    var strategy: StrategyType = StrategyType.NONE,
    var amount: Int = 0,
    var expirationLimit: Long = 0,
    var discountedCategories: Set<ProductCategory> = mutableSetOf()
)