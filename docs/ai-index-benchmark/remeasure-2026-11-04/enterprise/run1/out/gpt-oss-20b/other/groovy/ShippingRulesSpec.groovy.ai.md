### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:10:26Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Tests shipping cost calculation and overload handling for DHL parcels.

#### Purpose
- Verify free shipping threshold logic.
- Validate tiered pricing by weight.
- Ensure overweight parcels throw `ParcelTooHeavyException`.

#### Type
- `class ShippingRulesSpec` – test specification (extends `Specification`).
- `class ShippingRules` – shipping cost calculator (`@CompileStatic`).

#### Input
- `ShippingRulesSpec`:
  - `rules` instance (`carrier: 'DHL', maxParcelKg: 31.5`).
  - Test orders (`Order(total: …, weightKg: …)`).
- `ShippingRules`:
  - `carrier: String`
  - `maxParcelKg: BigDecimal`
  - `costFor(Order order)` parameters.

#### Output
- `ShippingRules.costFor` returns `BigDecimal` shipping cost or throws `ParcelTooHeavyException`.
- Test methods assert returned cost or exception.

#### Core logic
- `costFor`:
  1. If `order.weightKg > maxParcelKg` → throw `ParcelTooHeavyException`.
  2. If `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` → return `0`.
  3. Define tiers: `[upTo: 2, price: 4.99]`, `[upTo: 10, price: 9.99]`, `[upTo: 31.5, price: 19.99]`.
  4. Find first tier where `order.weightKg <= tier.upTo` and return `tier.price`.

#### Public API
- `ShippingRules.costFor(Order) -> BigDecimal` – compute shipping cost.
- `ShippingRulesSpec.orders above the threshold ship for free() -> void` – test.
- `ShippingRulesSpec.a parcel of #weight kg costs #expected() -> void` – parameterized test.
- `ShippingRulesSpec.an overweight parcel is rejected() -> void` – test.
- `ShippingRulesSpec.heavyOrder(BigDecimal) -> Order` – helper (private).

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `Order` (external domain class)
- `ParcelTooHeavyException` (external exception)

#### Exceptions / Errors
- `ParcelTooHeavyException` thrown when parcel weight exceeds `maxParcelKg`.

#### Concurrency
- None.
