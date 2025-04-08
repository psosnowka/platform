package com.task.platform.domain

interface ProductRepository {
    fun findProductById(productId: ProductId): Product?
}
