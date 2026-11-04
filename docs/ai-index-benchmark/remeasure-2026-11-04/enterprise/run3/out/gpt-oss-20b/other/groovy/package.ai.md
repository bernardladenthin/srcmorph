### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:24:30Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> Provides shipping cost calculation logic and tests for DHL parcel shipping rules.

#### Purpose
- Verify shipping cost calculation, free‑shipping threshold, and overweight rejection for DHL parcels.

#### Responsibilities
- Compute shipping cost based on parcel weight and order total.
- Enforce free shipping for orders above a threshold.
- Reject parcels exceeding the maximum allowed weight.

#### Key units
- **ShippingRules** – calculates `BigDecimal costFor(Order order)`; throws `ParcelTooHeavyException`.
- **ShippingRulesSpec** – Spock specification testing `ShippingRules`.
- **Order** – domain object with `total` and `weightKg`.
- **ParcelTooHeavyException** – exception for overweight parcels.

#### Data flow
`Order` → `ShippingRules.costFor` → `BigDecimal cost` or `ParcelTooHeavyException`.

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`, `spock.lang.Unroll`
- External types: `Order`, `ParcelTooHeavyException`

#### Cross-cutting
- Static type checking (`@CompileStatic`).
- Spock parameterized tests (`@Unroll`).
- Exception handling for overweight parcels.
