package com.task.platform.domain

data class ProductNotFoundException(val id: ProductId) : RuntimeException()
