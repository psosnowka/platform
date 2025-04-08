package com.task.platform.application

import com.task.platform.BaseApplicationIntegrationSpec
import com.task.platform.application.api.ApiErrorsResponse
import com.task.platform.application.api.PriceCalculatedResponse
import com.task.platform.application.api.PriceCalculationRequest

class ProductControllerIntegrationTest_priceCalculation extends BaseApplicationIntegrationSpec implements ProductsTrait {

    def "should return product by id"() {
        given:
        UUID productId = UUID.randomUUID()
        productExists {
            withId(productId)
            butPrice {
                withAmount(100.00)
                withCurrency("USD")
            }
        }

        and:
        def request = new PriceCalculationRequest(10L)


        when:
        def response = calculateProductPriceHttpCall(productId.toString(), request)

        then:
        response.then()
                .assertThat()
                .statusCode(200)

        and:
        with(response.body().as(PriceCalculatedResponse)) {
            assert totalPrice.currency == "USD"
            assert totalPrice.amount == 800.00
        }

    }

    def "should return 404 when product does not exist"() {
        given:
        UUID productId = UUID.randomUUID()
        def request = new PriceCalculationRequest(10L)

        when:
        def response = calculateProductPriceHttpCall(productId.toString(), request)

        then:
        response.then()
                .assertThat()
                .statusCode(404)
        and:
        with(response.body().as(ApiErrorsResponse)) {
            def error = it.errors.first
            assert error.code == "product_not_found"
            assert error.message == "Product not found for id: ${productId}"
        }
    }

    def "should return 400 when request id is invalid"() {
        given:
        def invalidId = "invalid-id"
        def request = new PriceCalculationRequest(10L)

        when:
        def response = calculateProductPriceHttpCall(invalidId.toString(), request)

        then:
        response.then()
                .assertThat()
                .statusCode(400)

        with(response.body().as(ApiErrorsResponse)) {
            def error = it.errors.first
            assert error.code == "invalid_request"
            assert error.message == "Invalid value: $invalidId, calculateProductPrice.id Invalid UUID format"
        }
    }

    def "should return 400 when quantity is lower than 0"() {
        given:
        UUID productId = UUID.randomUUID()
        def request = new PriceCalculationRequest(-5L)

        when:
        def response = calculateProductPriceHttpCall(productId.toString(), request)

        then:
        response.then()
                .assertThat()
                .statusCode(400)

        and:
        with(response.body().as(ApiErrorsResponse)) {
            def error = it.errors.first
            assert error.code == "invalid_request"
            assert error.message == "Invalid value: -5,must be greater than or equal to 0"
        }
    }

}
