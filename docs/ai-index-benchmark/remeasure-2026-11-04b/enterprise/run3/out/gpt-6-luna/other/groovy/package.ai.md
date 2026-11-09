### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:39:43Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> Calculates order delivery charges from parcel weight and order value, including free-shipping eligibility and rejection of overweight parcels.

#### Purpose
- Determines shipping costs using order totals, parcel weights, and configured carrier limits.
- Specifies expected free-shipping, weight-tier pricing, and overweight-rejection behavior.

#### Responsibilities
- **Shipping calculation:** `ShippingRules` checks the parcel weight limit, applies free shipping above the threshold, and otherwise selects a weight-based price tier.
- **Behavior verification:** `ShippingRulesSpec` tests free shipping, tier prices for weights 1, 10, and 30 kg, and rejection of a 40 kg parcel.

#### Key units
- `ShippingRules` — holds carrier and maximum parcel weight settings; calculates a shipping price or throws `ParcelTooHeavyException`.
- `ShippingRulesSpec` — Spock specification for shipping rule behavior; defines `FREE_SHIPPING_THRESHOLD` and a `heavyOrder` fixture helper.

#### Data flow
- `costFor(Order)` checks `Order.weightKg` against `maxParcelKg`; over-limit parcels raise `ParcelTooHeavyException`.
- For parcels within the limit, `Order.total` at or above the free-shipping threshold yields `0`; otherwise, the first matching weight tier supplies the price.

#### Dependencies
- Domain types: `Order`, `ParcelTooHeavyException`.
- Test framework: `spock.lang.Specification`, `spock.lang.Unroll`.
- Language support: `groovy.transform.CompileStatic`.

#### Cross-cutting
- `ShippingRules` uses `@CompileStatic`.
- Shipping tiers are defined by maximum weights and prices: 2 kg / 4.99, 10 kg / 9.99, and 31.5 kg / 19.99.
