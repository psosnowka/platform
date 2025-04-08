package com.task.platform.domain


import spock.lang.Specification
import spock.lang.Subject

class ProductServiceTest extends Specification {

    private InMemoryProductRepository productRepository = new InMemoryProductRepository()
    private DiscountConfigurationProvider discountConfigurationRepository = Mock(DiscountConfigurationProvider)

    @Subject
    private ProductService sut = new ProductService(
            productRepository,
            discountConfigurationRepository
    )

    private UUID productId = UUID.randomUUID()


    def 'should calculate price with discount'() {
        given:
        productRepository.exists {
            withId(productId)
            butPrice {
                withAmount(19.55)
                withCurrency("USD")
            }
        }

        and:
        def discountProvider = new DiscountConfiguration.SingleDiscountRule(
                new SingleDiscount.Percentage(0.20)
        )
        discountConfigurationRepository.getDiscountConfiguration() >> discountProvider

        and:
        def productQuantity = new CalculateProductsPrice(
                new ProductId(productId),
                10L
        )

        when:
        def result = sut.calculatePrice(productQuantity)

        then:
        with(result.amount) {
            assert it.amount == 156.40
            assert it.currency == "USD"
        }
    }

    def 'should throw exception when product not found'() {
        given:
        def productQuantity = new CalculateProductsPrice(
                new ProductId(productId),
                10L
        )

        when:
        sut.calculatePrice(productQuantity)

        then:
        def ex = thrown(ProductNotFoundException)
        ex.id == productQuantity.productId
    }

}
