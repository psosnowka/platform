package com.task.platform.application.api

import com.task.platform.domain.Money
import java.math.BigDecimal

data class MoneyResponse(
    val amount: BigDecimal,
    val currency: String,
) {
    companion object {
        fun from(money: Money): MoneyResponse =
            MoneyResponse(
                amount = money.amount,
                currency = money.currency,
            )
    }
}
