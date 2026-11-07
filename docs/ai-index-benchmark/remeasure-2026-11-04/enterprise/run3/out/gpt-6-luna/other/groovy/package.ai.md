### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:21:36Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> Calculates parcel shipping charges from order value and weight, including free-shipping eligibility and rejection of parcels above the carrier’s weight limit.

#### Purpose
- Implements shipping-cost rules and verifies them against free-shipping, weight-tier, and overweight cases.

#### Responsibilities
- Apply a maximum parcel weight and order-value threshold before selecting a paid shipping tier.
- Test the rules with Spock examples, including data-driven checks for paid tiers.

#### Key units
- `ShippingRules` — calculates shipping cost, with `carrier` and `maxParcelKg` properties.
- `ShippingRulesSpec` — tests free shipping at or above `50.00`, paid tiers (`4.99`, `9.99`, `19.99`), and overweight rejection.

#### Data flow
- `costFor(Order)` checks `weightKg` against `maxParcelKg`, then checks `total` against the free-shipping threshold; otherwise it returns the first matching weight-tier price.
- Overweight orders raise `ParcelTooHeavyException`; the spec verifies the resulting behavior.

#### Dependencies
- `Order` supplies order weight and total; `ParcelTooHeavyException` represents overweight rejection.
- `ShippingRulesSpec` uses `spock.lang.Specification` and `spock.lang.Unroll`; `ShippingRules` uses `groovy.transform.CompileStatic`.

#### Cross-cutting
- `ShippingRules` is statically compiled; shipping rules are exercised through Spock feature methods, including an unrolled tier test.
