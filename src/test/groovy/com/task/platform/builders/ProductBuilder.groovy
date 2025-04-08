package com.task.platform.builders

import com.task.platform.application.ProductEntity
import com.task.platform.domain.Product
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class ProductBuilder {
    UUID id
    MoneyBuilder price

    def butPrice(@DelegatesTo(MoneyBuilder) Closure<MoneyBuilder> closure) {
        withPrice(MoneyBuilder.create(100.00, "USD").with closure)
        this
    }

    Product build() {
        new Product(
                id,
                price?.build()
        )
    }

    ProductEntity buildEntity() {
        new ProductEntity(
                id,
                price.amount,
                price.currency
        )
    }

}

class ProductBuilderFixture {

    static ProductBuilder validProduct() {
        new ProductBuilder()
                .withId(UUID.randomUUID())
                .withPrice(MoneyBuilder.create(100.00, "USD"))
    }
}