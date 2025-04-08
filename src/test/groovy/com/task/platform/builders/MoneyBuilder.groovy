package com.task.platform.builders

import com.task.platform.domain.Money
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class MoneyBuilder {
    BigDecimal amount
    String currency

    Money build() {
        new Money(amount, currency)
    }

    static MoneyBuilder create(BigDecimal amount, String currency) {
        new MoneyBuilder()
                .withAmount(100.00)
                .withCurrency("USD")
    }

}
