package com.task.platform.application

import com.task.platform.domain.Money
import com.task.platform.domain.Product
import com.task.platform.domain.ProductId
import com.task.platform.domain.ProductRepository
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
class JpaProductRepository(
    private val productsSpringDataCrudRepository: ProductsSpringDataCrudRepository
) : ProductRepository {

    override fun findProductById(productId: ProductId): Product? {
        return productsSpringDataCrudRepository.findFirstById(productId.id)?.toDomain()
    }
}


interface ProductsSpringDataCrudRepository : CrudRepository<ProductEntity, UUID> {
    fun findFirstById(id: UUID): ProductEntity?
}

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val priceAmount: BigDecimal,
    val priceCurrency: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun toDomain(): Product =
        Product(
            id = id,
            price = Money(
                amount = priceAmount,
                currency = priceCurrency
            )
        )


    companion object {
        fun of(
            product: Product
        ): ProductEntity {
            return ProductEntity(
                id = product.id,
                priceAmount = product.price.amount,
                priceCurrency = product.price.currency
            )
        }

    }
}