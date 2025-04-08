package com.task.platform.domain

import spock.lang.Specification

class DiscountConfigurationTest extends Specification {

    def 'should calculate cumulative discount for quantity: #quantity'() {
        given:
        def discountProvider = new DiscountConfiguration.CumulativeDiscountsRule([
                new SingleDiscount.Percentage(0.10),
                new SingleDiscount.Quantity([
                        new SingleDiscount.Quantity.Rule(1, 5, 0.99),
                        new SingleDiscount.Quantity.Rule(6, 10, 0.50),
                        new SingleDiscount.Quantity.Rule(11, Long.MAX_VALUE, 0.10),
                ])
        ])

        when:
        def discount = discountProvider.calculateDiscountPercentage(quantity)

        then:
        discount == discountResult

        where:
        quantity || discountResult
        1        || 1.00
        6        || 0.60
        10       || 0.60
        123      || 0.20
    }

    def 'should calculate with Single Rule Quantity based for quantity: #quantity'() {
        given:
        def discountProvider = new DiscountConfiguration.SingleDiscountRule(
                new SingleDiscount.Quantity([
                        new SingleDiscount.Quantity.Rule(1, 10, 1.99),
                        new SingleDiscount.Quantity.Rule(11, 14, 0.50),
                        new SingleDiscount.Quantity.Rule(20, Long.MAX_VALUE, 0.10),
                ])
        )

        when:
        def discount = discountProvider.calculateDiscountPercentage(quantity)

        then:
        discount == discountResult

        where:
        quantity || discountResult
        1        || 1.00
        11       || 0.50
        14       || 0.50
        15       || 0.00
        20       || 0.10
    }

    def 'should calculate with Single Rule Percentage based for quantity: #quantity'() {
        given:
        def discountProvider = new DiscountConfiguration.SingleDiscountRule(
                new SingleDiscount.Percentage(0.11)
        )

        when:
        def discount = discountProvider.calculateDiscountPercentage(quantity)

        then:
        discount == discountResult

        where:
        quantity || discountResult
        1        || 0.11
        11       || 0.11
        14       || 0.11

    }

    def 'should calculate with Highest Discount rule for quantity: #quantity'() {
        given:
        def discountProvider = new DiscountConfiguration.HighestDiscountRule([
                new SingleDiscount.Percentage(0.30),
                new SingleDiscount.Quantity([
                        new SingleDiscount.Quantity.Rule(1, 5, 0.99),
                        new SingleDiscount.Quantity.Rule(6, 10, 0.50),
                        new SingleDiscount.Quantity.Rule(11, Long.MAX_VALUE, 0.10),
                ])
        ])

        when:
        def discount = discountProvider.calculateDiscountPercentage(quantity)

        then:
        discount == discountResult

        where:
        quantity || discountResult
        1        || 0.99
        7        || 0.50
        123      || 0.30
    }

}
