package com.task.platform.domain

import java.math.BigDecimal

interface DiscountConfigurationProvider {
    fun getDiscountConfiguration(): DiscountConfiguration
}

sealed class DiscountConfiguration {

    abstract fun calculateDiscountPercentage(quantity: Long): BigDecimal

    protected fun getSingleDiscount(singleDiscount: SingleDiscount, quantity: Long): BigDecimal =
        when (singleDiscount) {
            is SingleDiscount.Percentage -> singleDiscount.amount
            is SingleDiscount.Quantity -> singleDiscount.getDiscountPercentage(quantity)
        }

    data class CumulativeDiscountsRule(val discounts: List<SingleDiscount>) : DiscountConfiguration() {
        override fun calculateDiscountPercentage(quantity: Long): BigDecimal {
            return discounts.sumOf { getSingleDiscount(it, quantity) }.applyMaxDiscount()
        }
    }

    data class HighestDiscountRule(val discounts: List<SingleDiscount>) : DiscountConfiguration() {
        override fun calculateDiscountPercentage(quantity: Long): BigDecimal =
            discounts.maxOf { getSingleDiscount(it, quantity) }.applyMaxDiscount()
    }

    data class SingleDiscountRule(val discount: SingleDiscount) : DiscountConfiguration() {
        override fun calculateDiscountPercentage(quantity: Long): BigDecimal =
            getSingleDiscount(discount, quantity).applyMaxDiscount()
    }

    companion object {
        private val MAX_DISCOUNT = BigDecimal.ONE
    }

    protected fun BigDecimal.applyMaxDiscount(): BigDecimal =
        if (this >= MAX_DISCOUNT) {
            MAX_DISCOUNT
        } else {
            this
        }

}


sealed class SingleDiscount {

    data class Percentage(val amount: BigDecimal) : SingleDiscount()

    data class Quantity(val rules: List<Rule>) : SingleDiscount() {

        fun getDiscountPercentage(quantity: Long): BigDecimal {
            val percentage = rules
                .find { rule -> rule.canApply(quantity) }
            return percentage?.percentage ?: BigDecimal.ZERO
        }

        data class Rule(
            val min: Long,
            val max: Long,
            val percentage: BigDecimal
        ) {
            fun canApply(quantity: Long): Boolean =
                quantity in min..max

        }
    }

}
