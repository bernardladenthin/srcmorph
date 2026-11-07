### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:36:31Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> Calculates shipping cost for orders, applying weight tiers, free‑shipping thresholds, and rejecting overweight parcels.

#### Purpose
- Provide a reusable shipping cost calculator.
- Verify its behavior with Spock tests.

#### Responsibilities
- **ShippingRules**: encapsulates carrier info, maximum parcel weight, and cost calculation logic.
- **ShippingRulesSpec**: defines unit tests covering normal, free‑shipping, and overweight scenarios.

#### Key units
- `ShippingRules` – `@CompileStatic` class; `costFor(order: Order): BigDecimal` computes cost or throws `ParcelTooHeavyException`.
- `ShippingRulesSpec` – Spock specification; uses `@Unroll` to test multiple weight cases.

#### Data flow
1. `ShippingRules` instantiated with `carrier` and `maxParcelKg`.
2. `costFor` receives an `Order` (with `total` and `weightKg`).
3. Checks weight against `maxParcelKg`; throws if exceeded.
4. Checks `total` against `FREE_SHIPPING_THRESHOLD`; returns `0` if met.
5. Otherwise selects first tier where `weightKg <= tier.upTo` and returns the tier price.

#### Dependencies
- Groovy `@CompileStatic` for type safety.
- Spock framework (`Specification`, `Unroll`) for testing.
- Domain types: `shop.shipping.Order`, `shop.shipping.ParcelTooHeavyException`.

#### Cross-cutting
- Static type checking ensures compile‑time safety.
- Consistent exception handling via `ParcelTooHeavyException`.
- No concurrency; logic is deterministic and stateless.
