package com.task.platform.application

import com.task.platform.domain.DiscountConfigurationProvider
import com.task.platform.domain.ProductRepository
import com.task.platform.domain.ProductService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductConfiguration {

    @Bean
    fun productService(
        productRepository: ProductRepository,
        discountConfigurationProvider: DiscountConfigurationProvider
    ): ProductService {
        return ProductService(
            productRepository = productRepository,
            discountConfigurationProvider = discountConfigurationProvider,
        )
    }
}