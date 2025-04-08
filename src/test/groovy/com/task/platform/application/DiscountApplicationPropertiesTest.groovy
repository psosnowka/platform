package com.task.platform.application

import spock.lang.Specification

class DiscountApplicationPropertiesTest extends Specification {


    def "should throw IllegalArgumentException when discountsCombiningStrategy is SINGLE and discountsConfiguration size is not 1"() {
        when:
        new DiscountApplicationProperties(
                DiscountApplicationProperties.DiscountsCombiningStrategy.SINGLE,
                [new DiscountApplicationProperties.DiscountsConfiguration(
                        DiscountApplicationProperties.DiscountsConfiguration.DiscountType.PERCENTAGE,
                        BigDecimal.ONE,
                        null
                ), new DiscountApplicationProperties.DiscountsConfiguration(
                        DiscountApplicationProperties.DiscountsConfiguration.DiscountType.PERCENTAGE,
                        BigDecimal.ONE,
                        null
                )]
        )

        then:
        thrown(IllegalArgumentException)
    }

    def "should throw IllegalArgumentException when discountType is PERCENTAGE and discountPercentage is null"() {
        when:
        new DiscountApplicationProperties.DiscountsConfiguration(
                DiscountApplicationProperties.DiscountsConfiguration.DiscountType.PERCENTAGE,
                null,
                null
        )

        then:
        thrown(IllegalArgumentException)
    }

    def "should throw IllegalArgumentException when discountType is QUANTITY and rules are empty or null"() {
        when:
        new DiscountApplicationProperties.DiscountsConfiguration(
                DiscountApplicationProperties.DiscountsConfiguration.DiscountType.QUANTITY,
                null,
                rules
        )

        then:
        thrown(IllegalArgumentException)

        where:
        rules << [null, []]
    }

    def "should throw IllegalArgumentException when rule min is greater than max"() {
        when:
        new DiscountApplicationProperties.DiscountsConfiguration.Rule(
                min,
                max,
                BigDecimal.ONE
        )

        then:
        thrown(IllegalArgumentException)

        where:
        min | max
        10  | 5
        5   | 4
    }

    def "should throw IllegalArgumentException when rules have overlapping ranges"() {
        when:
        new DiscountApplicationProperties.DiscountsConfiguration(
                DiscountApplicationProperties.DiscountsConfiguration.DiscountType.QUANTITY,
                null,
                [
                        new DiscountApplicationProperties.DiscountsConfiguration.Rule(4, 10, BigDecimal.ONE),
                        new DiscountApplicationProperties.DiscountsConfiguration.Rule(1, 5, BigDecimal.ONE),
                ]
        )

        then:
        thrown(IllegalArgumentException)
    }
}
