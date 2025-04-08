package com.task.platform.domain

import java.util.UUID

data class Product(
    val id: UUID,
    val price: Money
)