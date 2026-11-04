### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:08:51Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> Calculates order shipping charges from order value and parcel weight, with free-shipping eligibility and overweight-parcel rejection.

#### Purpose
- Determine shipping costs using a free-shipping threshold and weight-based price tiers.
- Verify shipping rules for free shipping, tier pricing, and overweight parcels.

#### Responsibilities
- Shipping calculation: reject parcels above the configured weight limit, waive charges for qualifying orders, and otherwise apply the matching weight tier.
- Spock specifications: exercise free-shipping, tier-price, and overweight-rejection behavior.

#### Key units
- `ShippingRules` — calculates a `BigDecimal` charge in `costFor(Order order)` using `carrier` and `maxParcelKg` properties.
- `ShippingRulesSpec` — configures shipping rules and tests expected charges and rejection behavior; defines `FREE_SHIPPING_THRESHOLD`.
- `heavyOrder(BigDecimal kg)` — test helper that creates an order totaling 10.

#### Data flow
- `costFor` reads `Order.weightKg` and `Order.total`; it first rejects weight above `maxParcelKg`, then checks the free-shipping threshold, then selects a weight-tier charge.
- Returns `0` for qualifying orders or `4.99`, `9.99`, or `19.99` for parcels up to 2, 10, or 31.5 kg, respectively; overweight parcels raise `ParcelTooHeavyException`.

#### Dependencies
- Domain types: `Order`, `ParcelTooHeavyException`.
- Groovy and test libraries: `groovy.transform.CompileStatic`, `spock.lang.Specification`, `spock.lang.Unroll`.

#### Cross-cutting
- `ShippingRules` uses `@CompileStatic`.
- No concurrency or synchronization behavior is reported; no explicit null handling is defined.
