### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:31:00Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> Calculates parcel shipping charges from order value and weight, applying free-shipping eligibility, weight-tier rates, and carrier weight limits.

#### Purpose
- Defines `ShippingRules` shipping-charge behavior and verifies it with Spock tests.
- Covers free shipping, tiered pricing, and rejection of parcels exceeding the configured maximum weight.

#### Responsibilities
- Shipping calculation: `ShippingRules` checks parcel weight, waives charges for qualifying order totals, and selects a weight-tier price otherwise.
- Behavioral tests: `ShippingRulesSpec` asserts free-shipping, three weight/price pairs, and the overweight exception.

#### Key units
- `ShippingRules` — configures a carrier and maximum parcel weight, then computes a `BigDecimal` charge for an `Order`.
- `ShippingRules.costFor(Order)` — throws `ParcelTooHeavyException` for overweight parcels, returns zero at or above the `50.00` free-shipping threshold, or applies a tier price.
- `ShippingRulesSpec` — Spock specification using DHL and a `31.5` kg limit to test the shipping rules.
- `ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` — order-total threshold for free shipping (`50.00`).

#### Data flow
- `costFor` reads `Order.weightKg` and `Order.total`.
- It rejects weight above `maxParcelKg`; otherwise, it returns zero when the order total meets the threshold, or selects the first matching weight tier: up to 2 kg (`4.99`), 10 kg (`9.99`), or 31.5 kg (`19.99`).
- `ShippingRulesSpec` supplies test orders and checks resulting charges or the expected exception.

#### Dependencies
- Uses `Order`, `ParcelTooHeavyException`, and `BigDecimal`.
- Tests use Spock `Specification` and `Unroll`; `ShippingRules` uses Groovy `CompileStatic`.

#### Cross-cutting
- Shipping calculations use `BigDecimal` for weights, thresholds, and prices.
- `ShippingRules` is annotated `@CompileStatic`; tests use Spock assertions and exception expectations.
- No concurrency or explicit null handling is shown.
