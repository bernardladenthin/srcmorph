### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:37:53Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Shipping rules determine order delivery charges from parcel weight and order value, including free shipping and rejection of overweight parcels.

#### Purpose
- `ShippingRulesSpec` verifies free-shipping eligibility, weight-based prices, and overweight-parcel rejection.
- `ShippingRules` calculates shipping cost using carrier settings, parcel weight, and order total.

#### Type
- `ShippingRulesSpec` — class extending `Specification`; Spock specification.
- `ShippingRules` — class; `@CompileStatic`; no explicit superclass or interfaces.

#### Input
- `ShippingRules.carrier: String` and `ShippingRules.maxParcelKg: BigDecimal` — configurable properties.
- `ShippingRules.costFor(Order order)` — order total and weight.
- `ShippingRulesSpec` fixture initializes `rules` with carrier `'DHL'` and maximum parcel weight `31.5`.
- Test data supplies order totals and weights; `heavyOrder(BigDecimal kg)` constructs an order with total `10`.

#### Output
- `ShippingRules.costFor(Order)` returns a `BigDecimal` shipping price or throws `ParcelTooHeavyException`.
- `ShippingRulesSpec` assertions verify a zero price, tier prices, and the overweight exception.

#### Core logic
- `costFor` first compares `order.weightKg` with `maxParcelKg`; if over the limit, throws `ParcelTooHeavyException` with parcel and limit weights.
- If `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD`, returns `0`.
- Otherwise defines `tiers` with maximum weights/prices `(2, 4.99)`, `(10, 9.99)`, and `(31.5, 19.99)`; finds the first tier where `order.weightKg <= tier.upTo` and casts its `price` to `BigDecimal`.
- `ShippingRulesSpec` checks free shipping for total `75.00`; unrolled cases check weights `1`, `10`, and `30`; overweight case checks weight `40`.
- `heavyOrder` constructs `new Order(total: 10, weightKg: kg)`; it is not called by the shown tests.

#### Public API
- `FREE_SHIPPING_THRESHOLD: BigDecimal` — free-shipping cutoff.
- `rules: ShippingRules` — specification fixture.
- `"orders above the threshold ship for free"()` -> `void` — verifies free shipping.
- `"a parcel of #weight kg costs #expected"()` -> `void` — verifies tier prices.
- `"an overweight parcel is rejected"()` -> `void` — verifies overweight rejection.
- `getCarrier()`/`setCarrier(String)` -> `String` — carrier property.
- `getMaxParcelKg()`/`setMaxParcelKg(BigDecimal)` -> `BigDecimal` — maximum parcel weight.
- `costFor(Order)` -> `BigDecimal` — calculate shipping cost.

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `Order`
- `ParcelTooHeavyException`

#### Exceptions / Errors
- Throws `ParcelTooHeavyException` when parcel weight exceeds `maxParcelKg`.
- No explicit handling for a weight that matches no tier after the maximum-weight check.
