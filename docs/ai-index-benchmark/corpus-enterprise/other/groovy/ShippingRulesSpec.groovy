// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package shop.shipping

import groovy.transform.CompileStatic
import spock.lang.Specification
import spock.lang.Unroll

/*
 * class ShippingRulesLegacySpec was merged into this one.
 */
class ShippingRulesSpec extends Specification {

    static final BigDecimal FREE_SHIPPING_THRESHOLD = 50.00

    def rules = new ShippingRules(carrier: 'DHL', maxParcelKg: 31.5)

    def "orders above the threshold ship for free"() {
        given:
        def order = new Order(total: 75.00, weightKg: 2)

        when:
        def cost = rules.costFor(order)

        then:
        cost == 0
    }

    @Unroll
    def "a parcel of #weight kg costs #expected"() {
        expect:
        rules.costFor(new Order(total: 10, weightKg: weight)) == expected

        where:
        weight | expected
        1      | 4.99
        10     | 9.99
        30     | 19.99
    }

    def "an overweight parcel is rejected"() {
        when:
        rules.costFor(new Order(total: 10, weightKg: 40))

        then:
        thrown(ParcelTooHeavyException)
    }

    private Order heavyOrder(BigDecimal kg) {
        new Order(total: 10, weightKg: kg)
    }
}

@CompileStatic
class ShippingRules {
    String carrier
    BigDecimal maxParcelKg

    BigDecimal costFor(Order order) {
        if (order.weightKg > maxParcelKg) {
            throw new ParcelTooHeavyException("parcel of ${order.weightKg} kg exceeds ${maxParcelKg}")
        }
        if (order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD) {
            return 0
        }
        def tiers = [[upTo: 2, price: 4.99], [upTo: 10, price: 9.99], [upTo: 31.5, price: 19.99]]
        tiers.find { tier -> order.weightKg <= tier.upTo }.price as BigDecimal
    }
}
