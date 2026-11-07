### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:15:23Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> Calculates order shipping charges by applying a free-shipping threshold, weight-based price tiers, and a maximum parcel-weight limit.

#### Purpose
- Provides `ShippingRules.costFor(Order)` to determine shipping charges.
- Verifies free-shipping, tier pricing, and overweight rejection behavior with Spock tests.

#### Responsibilities
- **Shipping calculation:** waive shipping for orders totaling at least `50.00`; otherwise select a price from ascending weight tiers.
- **Behavior verification:** `ShippingRulesSpec` checks representative prices and that a `40 kg` parcel is rejected.

#### Key units
- `ShippingRules` — holds carrier and maximum parcel weight; calculates an `Order`’s shipping charge.
- `ShippingRulesSpec` — Spock specification covering free shipping, tier prices, and overweight parcels.
- `FREE_SHIPPING_THRESHOLD` — `BigDecimal` cutoff for free shipping.
- `ParcelTooHeavyException` — signals weight above `maxParcelKg`.

#### Data flow
- `costFor(Order)` reads `weightKg` and `total`; it rejects parcels above the configured maximum, returns zero for qualifying order totals, or selects a price from the `2 kg`, `10 kg`, and `31.5 kg` tiers.
- The specification configures DHL rules with a `31.5 kg` maximum and checks returned prices or the overweight exception.

#### Dependencies
- `Order` supplies the order total and parcel weight.
- `ParcelTooHeavyException` represents overweight rejection.
- `spock.lang.Specification` and `spock.lang.Unroll` support the tests; `groovy.transform.CompileStatic` annotates `ShippingRules`.

#### Cross-cutting
- Shipping calculation has no persistent writes, state mutation, or concurrency behavior.
- If weight is within `maxParcelKg` but no price tier matches, the summary reports that accessing `.price` on the null tier result fails.
