package com.task.platform.application

import com.task.platform.domain.DiscountConfigurationProvider
import com.task.platform.domain.DiscountConfiguration
import com.task.platform.domain.SingleDiscount
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.util.Assert
import java.math.BigDecimal

@Configuration
class DiscountApplicationPropertiesProvider(
    private val discountApplicationProperties: DiscountApplicationProperties,
) : DiscountConfigurationProvider {


    override fun getDiscountConfiguration(): DiscountConfiguration {
        return when (discountApplicationProperties.combiningStrategy) {
            DiscountApplicationProperties.DiscountsCombiningStrategy.SINGLE -> singleProvider()
            DiscountApplicationProperties.DiscountsCombiningStrategy.CUMULATIVE -> cumulativeProvider()
            DiscountApplicationProperties.DiscountsCombiningStrategy.HIGHEST -> highestProvider()
        }
    }

    private fun singleProvider(): DiscountConfiguration =
        DiscountConfiguration.SingleDiscountRule(
            discount = discountApplicationProperties.configurations.map { it.toDiscount() }.first()
        )

    private fun cumulativeProvider(): DiscountConfiguration =
        DiscountConfiguration.CumulativeDiscountsRule(
            discounts = discountApplicationProperties.configurations.map { it.toDiscount() }
        )

    private fun highestProvider(): DiscountConfiguration =
        DiscountConfiguration.HighestDiscountRule(
            discounts = discountApplicationProperties.configurations.map { it.toDiscount() }
        )
}

private fun DiscountApplicationProperties.DiscountsConfiguration.toDiscount(): SingleDiscount {
    return when (this.discountType) {
        DiscountApplicationProperties.DiscountsConfiguration.DiscountType.PERCENTAGE ->
            SingleDiscount.Percentage(
                this.discountPercentage ?: BigDecimal.ONE,
            )

        DiscountApplicationProperties.DiscountsConfiguration.DiscountType.QUANTITY ->
            SingleDiscount.Quantity(
                rules = this.rules?.map { rule ->
                    SingleDiscount.Quantity.Rule(
                        min = rule.min,
                        max = rule.max ?: Long.MAX_VALUE,
                        percentage = rule.discountPercentage,
                    )
                } ?: emptyList()
            )
    }
}


@ConfigurationProperties(prefix = "discount")
data class DiscountApplicationProperties(
    val combiningStrategy: DiscountsCombiningStrategy,
    val configurations: List<DiscountsConfiguration>,
) {

    data class DiscountsConfiguration(
        val discountType: DiscountType,
        val discountPercentage: BigDecimal?,
        val rules: List<Rule>?,
    ) {

        enum class DiscountType {
            PERCENTAGE,
            QUANTITY
        }

        data class Rule(
            val min: Long,
            val max: Long?,
            val discountPercentage: BigDecimal,
        ) {
            init {
                Assert.isTrue(
                    min <= (max ?: Long.MAX_VALUE),
                    "For $this rule Min value must be less than or equal to max value"
                )
            }
        }

        init {
            when (discountType) {
                DiscountType.PERCENTAGE -> {
                    Assert.notNull(
                        discountPercentage,
                        "Discount percentage must not be null for PERCENTAGE discount type"
                    )
                }

                DiscountType.QUANTITY -> {
                    Assert.notEmpty(
                        rules,
                        "Rules must not be empty for QUANTITY discount type"
                    )
                    assertThatRulesHaveDisjointRanges(rules!!)
                }
            }
        }

        private fun assertThatRulesHaveDisjointRanges(rules: List<Rule>) {
            val sortedRules = rules.sortedBy { it.min }
            for (i in 0 until sortedRules.size - 1) {
                val currentRule = sortedRules[i]
                val nextRule = sortedRules[i + 1]
                Assert.isTrue(
                    currentRule.max == null || currentRule.max <= nextRule.min,
                    "Max value of rule $currentRule must be greater than or equal to min value of rule $nextRule"
                )
            }
        }
    }

    enum class DiscountsCombiningStrategy {
        SINGLE,
        CUMULATIVE,
        HIGHEST,
    }

    init {
        if (combiningStrategy == DiscountsCombiningStrategy.SINGLE) {
            Assert.notNull(configurations, "Discount configuration must not be empty")
            Assert.isTrue(
                configurations.size == 1,
                "Only one discount configuration is allowed for SINGLE discountsCombiningStrategy"
            )

        }
    }
}


