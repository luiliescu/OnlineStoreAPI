package org.example.onlinestoreapi.strategies

import org.example.onlinestoreapi.entities.Product
import org.example.onlinestoreapi.entities.ProductCategory

class CategoryDiscountStrategy(val discountedCategories: Set<ProductCategory>) : DiscountStrategy {
    override fun applyDiscount(product: Product, discount: Int): Product {
        return if (product.category in discountedCategories) {
            product.copy(price = product.price * (100f - discount) / 100)
        } else {
            product
        }
    }
}
