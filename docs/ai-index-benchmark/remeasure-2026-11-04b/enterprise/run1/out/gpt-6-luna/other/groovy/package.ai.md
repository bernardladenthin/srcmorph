### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:24:32Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> Determines whether an order qualifies for free delivery, calculates weight-based shipping charges, and rejects parcels exceeding the configured weight limit.

#### Purpose
- Calculates shipping costs from order value and parcel weight.
- Specifies expected behavior for free shipping, weight-tier rates, and overweight parcels.

#### Responsibilities
- **Shipping rules:** Apply the parcel weight limit, free-shipping threshold, and weight-tier pricing.
- **Behavioral tests:** Use Spock examples to verify free delivery, unrolled weight-based prices, and overweight rejection.

#### Key units
- `ShippingRules` — stores carrier and maximum parcel weight; `costFor(Order)` returns a `BigDecimal` charge or throws `ParcelTooHeavyException`.
- `ShippingRulesSpec` — Spock specification defining a `50.00` free-shipping threshold and testing shipping-rule outcomes.
- `Order` — supplies the `weightKg` and `total` used by the rules.
- `ParcelTooHeavyException` — signals that an order exceeds the configured maximum parcel weight.

#### Data flow
- `costFor(Order)` checks `weightKg` against `maxParcelKg` and rejects overweight orders.
- If within the limit, it returns zero when `total` meets or exceeds `FREE_SHIPPING_THRESHOLD`; otherwise it selects the first matching tier: up to 2 kg (`4.99`), 10 kg (`9.99`), or 31.5 kg (`19.99`).

#### Dependencies
- `Order` and `ParcelTooHeavyException` from `shop.shipping`.
- `groovy.transform.CompileStatic` for the rules implementation; Spock `Specification` and `Unroll` for tests.

#### Cross-cutting
- `ShippingRules` is annotated with `@CompileStatic`; tests use Spock feature methods and `@Unroll`.
- A parcel within `maxParcelKg` but beyond all listed pricing tiers may yield no tier match before `.price` access.
