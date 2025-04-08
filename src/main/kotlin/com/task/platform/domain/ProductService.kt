package com.task.platform.domain

class ProductService(
    private val productRepository: ProductRepository,
    private val discountConfigurationProvider: DiscountConfigurationProvider,
) {
    fun getProductById(productId: ProductId): Product =
        findProduct(productId)


    fun calculatePrice(calculateProductsPrice: CalculateProductsPrice): ProductPrice {
        val product = findProduct(calculateProductsPrice.productId)
        val totalPrice = product.price *  calculateProductsPrice.quantity
        val discount = discountConfigurationProvider.getDiscountConfiguration()
            .calculateDiscountPercentage(calculateProductsPrice.quantity)
        val priceAfterDiscount = totalPrice * (1.00.toBigDecimal() - discount)
        return ProductPrice(priceAfterDiscount)
    }

    private fun findProduct(productId: ProductId): Product =
        productRepository.findProductById(productId) ?: throw ProductNotFoundException(
            productId
        )


}
