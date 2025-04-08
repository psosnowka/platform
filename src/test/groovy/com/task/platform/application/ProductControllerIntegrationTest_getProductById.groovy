package com.task.platform.application

import com.task.platform.BaseApplicationIntegrationSpec
import com.task.platform.application.api.ApiErrorsResponse
import com.task.platform.application.api.ProductResponse

class ProductControllerIntegrationTest_getProductById extends BaseApplicationIntegrationSpec implements ProductsTrait {

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

        when:
        def response = getProductByIdHttpCall(productId.toString())

        then:
        response.then()
                .assertThat()
                .statusCode(200)

        and:
        with(response.body().as(ProductResponse)) {
            assert it.id == productId
            assert it.money.currency == "USD"
            assert it.money.amount == 100.00
        }

    }

    def "should return 404 when product does not exist"() {
        given:
        UUID productId = UUID.randomUUID()

        when:
        def response = getProductByIdHttpCall(productId.toString())

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

        when:
        def response = getProductByIdHttpCall(invalidId)

        then:
        response.then()
                .assertThat()
                .statusCode(400)

        with(response.body().as(ApiErrorsResponse)) {
            def error = it.errors.first
            assert error.code == "invalid_request"
            assert error.message == "Invalid value: $invalidId, getProductById.id Invalid UUID format"
        }
    }
}
