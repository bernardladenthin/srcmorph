### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:18:17Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> A lightweight shipping‑cost engine for DHL parcels, with unit tests verifying free‑shipping thresholds, tiered rates, and overweight rejection.

#### Purpose
- Provide a cost calculation service for DHL parcels.
- Test the service’s free‑shipping logic, weight tiers, and exception handling.

#### Responsibilities
- **ShippingRules**: compute shipping cost based on order total and weight, enforce maximum parcel weight.
- **ShippingRulesSpec**: validate ShippingRules behavior with Spock tests.

#### Key units
- `ShippingRules` (`@CompileStatic`): `costFor(Order)` → `BigDecimal`; throws `ParcelTooHeavyException` if `order.weightKg > maxParcelKg`.
- `ShippingRulesSpec` (`Specification`): tests for free shipping, tiered pricing, and overweight rejection; helper `heavyOrder(BigDecimal)`.

#### Data flow
1. Test creates `Order(total, weightKg)`.
2. `ShippingRules.costFor` checks weight against `maxParcelKg`.
3. If within limits, checks `total` against `FREE_SHIPPING_THRESHOLD` (50.00) → returns 0 if met.
4. Otherwise selects first tier where `weightKg <= tier.upTo` and returns corresponding price (4.99, 9.99, 19.99).

#### Dependencies
- Groovy `@CompileStatic`.
- Spock `Specification`, `Unroll`.
- Domain types: `Order`, `ParcelTooHeavyException` (assumed in same package).

#### Cross-cutting
- Static type checking via `@CompileStatic`.
- Exception handling limited to `ParcelTooHeavyException`.
- No concurrency or external services involved.
