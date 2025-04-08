package com.task.platform.application

import com.task.platform.builders.ProductBuilder
import com.task.platform.builders.ProductBuilderFixture
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.springframework.beans.factory.annotation.Autowired

trait ProductsTrait {

    @Autowired
    ProductsSpringDataCrudRepository productsSpringDataCrudRepository

    Response getProductByIdHttpCall(String productId) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .get("/products/${productId}")
    }

    Response calculateProductPriceHttpCall(String productId, Object body) {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/products/${productId}/calculate-price")
    }


    def productExists(@DelegatesTo(ProductBuilder) Closure<ProductBuilder> closure) {
        def builder = ProductBuilderFixture.validProduct() with closure
        productsSpringDataCrudRepository.save(builder.buildEntity())
    }


}