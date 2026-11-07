### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:11:47Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> A Groovy test suite that verifies DHL shipping cost calculations, free‑shipping thresholds, tiered pricing, and overweight handling.

#### Purpose
- Validate DHL shipping cost logic.
- Ensure correct free‑shipping and tiered pricing behavior.
- Confirm overweight parcels trigger `ParcelTooHeavyException`.

#### Responsibilities
- **Testing**: Spock specification (`ShippingRulesSpec`) covering free shipping, tiered rates, and overweight rejection.
- **Calculation**: `ShippingRules` class implements `costFor(Order)` with weight limits, thresholds, and tiered pricing.

#### Key units
- **`ShippingRules`** (`@CompileStatic`): `costFor(Order)` → `BigDecimal`; throws `ParcelTooHeavyException` if `order.weightKg > maxParcelKg`.
- **`ShippingRulesSpec`** (`Specification`): parameterized tests (`@Unroll`) for tiered rates, free‑shipping threshold, and overweight rejection; helper `heavyOrder(BigDecimal)`.

#### Data flow
1. `ShippingRulesSpec` creates a `ShippingRules` instance with `carrier: 'DHL'` and `maxParcelKg: 31.5`.
2. Test orders (`Order(total, weightKg)`) are passed to `ShippingRules.costFor`.
3. `costFor` checks weight against `maxParcelKg`, applies free‑shipping threshold, selects appropriate tier, and returns the price or throws an exception.
4. Assertions in the spec verify returned cost or exception.

#### Dependencies
- **Internal**: `Order` domain class, `ParcelTooHeavyException`.
- **External**: Groovy `@CompileStatic`, Spock `Specification`, `Unroll`.

#### Cross-cutting
- Static type checking via `@CompileStatic`.
- Parameterized testing with Spock’s `@Unroll`.
- Consistent use of `Order` objects and exception handling across tests.
