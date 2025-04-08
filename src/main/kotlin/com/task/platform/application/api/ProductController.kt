package com.task.platform.application.api

import com.task.platform.domain.CalculateProductsPrice
import com.task.platform.domain.Product
import com.task.platform.domain.ProductId
import com.task.platform.domain.ProductService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.jetbrains.annotations.NotNull
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@Validated
@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/{id}")
    fun getProductById(@PathVariable @ValidUUID id: String): ProductResponse {
        val createdUUID = UUID.fromString(id)
        return productService.getProductById(ProductId(createdUUID)).toResponse()
    }

    @PostMapping("/{id}/calculate-price")
    fun calculateProductPrice(
        @PathVariable @ValidUUID id: String,
        @RequestBody @Valid request: PriceCalculationRequest,
    ): PriceCalculatedResponse {
        val createdUUID = UUID.fromString(id)
        val price = productService.calculatePrice(
            CalculateProductsPrice(
                productId = ProductId(createdUUID),
                quantity = request.quantity!!,
            )
        )
        return PriceCalculatedResponse(
            totalPrice = MoneyResponse.from(price.amount)
        )
    }
}

data class PriceCalculationRequest(
    @field:NotNull
    @field:Min(0)
    val quantity: Long?,
)

data class PriceCalculatedResponse(
    val totalPrice: MoneyResponse
)

private fun Product.toResponse(): ProductResponse = ProductResponse(
    id = id,
    money = MoneyResponse.from(price)
)

data class ProductResponse(
    val id: UUID,
    val money: MoneyResponse,
)

