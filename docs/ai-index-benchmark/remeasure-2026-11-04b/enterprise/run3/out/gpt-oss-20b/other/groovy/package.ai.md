### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:42:35Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> A Groovy module that defines a shipping cost engine and its Spock tests, applying free‑shipping thresholds and tiered pricing while rejecting overweight parcels.

#### Purpose
- Compute shipping cost for an order based on weight and total.
- Provide unit tests for the shipping rules.

#### Responsibilities
- **ShippingRules**: encapsulates carrier settings, maximum parcel weight, and tiered cost calculation.
- **ShippingRulesSpec**: Spock specification exercising the rule engine, including free‑shipping and overweight scenarios.

#### Key units
- `class ShippingRules` – `@CompileStatic`; fields `carrier:String`, `maxParcelKg:BigDecimal`; method `costFor(Order):BigDecimal` that throws `ParcelTooHeavyException` if `order.weightKg > maxParcelKg`, returns `0` for free shipping, otherwise selects the first tier where `order.weightKg <= tier.upTo` and returns its `price`.
- `class ShippingRulesSpec` – extends `Specification`; defines `FREE_SHIPPING_THRESHOLD`; tests `costFor` for normal, free‑shipping, and overweight orders using `@Unroll`.

#### Data flow
`ShippingRules` receives an `Order` (with `total` and `weightKg`), checks weight against `maxParcelKg`, applies free‑shipping threshold, then selects a tiered price based on weight, returning the cost or throwing an exception.

#### Dependencies
- Groovy `@CompileStatic` for type safety.
- Spock `Specification` and `Unroll` for testing.
- Domain types `shop.shipping.Order` and `shop.shipping.ParcelTooHeavyException` (assumed to exist elsewhere).

#### Cross-cutting
- Static type checking via `@CompileStatic`.
- No concurrency; all operations are stateless per instance.
- Exception handling limited to `ParcelTooHeavyException` for overweight parcels.
