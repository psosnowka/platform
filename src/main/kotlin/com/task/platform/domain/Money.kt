package com.task.platform.domain

import java.math.BigDecimal
import java.math.RoundingMode


data class Money(val amount: BigDecimal, val currency: String) {
    init {
        require(amount >= BigDecimal.ZERO) { "Amount must be non-negative" }
        require(currency.isNotEmpty()) { "Currency must not be empty" }
    }

    operator fun times(factor: BigDecimal): Money {
        val newAmount = (amount * factor).setScale(2, RoundingMode.HALF_UP)
        return Money(newAmount, currency)
    }

    operator fun times(factor: Long): Money {
        val newAmount = (amount * BigDecimal(factor)).setScale(2, RoundingMode.HALF_UP)
        return Money(newAmount, currency)
    }
}