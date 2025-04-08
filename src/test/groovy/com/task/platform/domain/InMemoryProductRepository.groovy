package com.task.platform.domain

import com.task.platform.builders.ProductBuilder
import com.task.platform.builders.ProductBuilderFixture
import org.jetbrains.annotations.NotNull

class InMemoryProductRepository implements ProductRepository {
    Map<UUID, Product> database = new HashMap<>()


    Product exists(@DelegatesTo(ProductBuilder) Closure<ProductBuilder> closure) {
        def product = ProductBuilderFixture.validProduct().with closure
        def build = product.build()
        database.put(product.id, build)
        return build
    }


    @Override
    Product findProductById(@NotNull ProductId productId) {
        return database.get(productId.id)
    }
}
