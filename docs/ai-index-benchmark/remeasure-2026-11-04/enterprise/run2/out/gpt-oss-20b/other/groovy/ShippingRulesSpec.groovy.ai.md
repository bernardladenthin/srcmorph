### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:17:02Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Tests shipping cost calculation and overweight handling for DHL parcels.

#### Purpose
- Verify free‑shipping threshold logic.
- Validate tiered shipping cost by weight.
- Ensure overweight parcels trigger `ParcelTooHeavyException`.

#### Type
- `class ShippingRulesSpec` (extends `Specification`) – test specification.
- `class ShippingRules` (annotated `@CompileStatic`) – shipping cost engine.

#### Input
- `ShippingRulesSpec`:
  - `FREE_SHIPPING_THRESHOLD` (BigDecimal, 50.00).
  - `rules` (ShippingRules, carrier: 'DHL', maxParcelKg: 31.5).
  - Test orders: `Order(total: BigDecimal, weightKg: BigDecimal)`.
- `ShippingRules`:
  - `carrier` (String).
  - `maxParcelKg` (BigDecimal).
  - `costFor(Order)` receives an `Order`.

#### Output
- `ShippingRulesSpec`:
  - Assertions on returned cost or thrown exception.
- `ShippingRules`:
  - Returns `BigDecimal` cost or throws `ParcelTooHeavyException`.

#### Core logic
- `ShippingRules.costFor`:
  1. If `order.weightKg > maxParcelKg` → throw `ParcelTooHeavyException`.
  2. If `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` → return 0.
  3. Define tiers: upTo 2 kg → 4.99, upTo 10 kg → 9.99, upTo 31.5 kg → 19.99.
  4. Find first tier where `order.weightKg <= tier.upTo` and return its `price`.

#### Public API
- `ShippingRulesSpec`:
  - `orders above the threshold ship for free() -> void` – test free shipping.
  - `a parcel of #weight kg costs #expected() -> void` – parameterized cost test.
  - `an overweight parcel is rejected() -> void` – test overweight exception.
  - `heavyOrder(BigDecimal) -> Order` – helper (private).
- `ShippingRules`:
  - `costFor(Order) -> BigDecimal` – compute shipping cost.

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `Order` (assumed in same package)
- `ParcelTooHeavyException` (assumed in same package)

#### Exceptions / Errors
- `ParcelTooHeavyException` thrown when `order.weightKg > maxParcelKg`.
- No other explicit exceptions.

#### Concurrency
- No threading or synchronization constructs used.
