### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:41:17Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Calculates shipping cost based on order weight and total, applying free‑shipping threshold and rejecting overweight parcels.

#### Purpose
- Implements shipping cost calculation with tiered pricing.
- Provides Spock tests for shipping rules.

#### Type
- `class ShippingRulesSpec` – Spock specification, extends `Specification`.
- `class ShippingRules` – shipping rule engine, `@CompileStatic`.
  - Fields: `String carrier`, `BigDecimal maxParcelKg`.
  - Method: `BigDecimal costFor(Order order)`.

#### Input
- `ShippingRules` constructor: `carrier`, `maxParcelKg`.
- `costFor` parameters: `Order order` with `total`, `weightKg`.
- Spec test data: `Order(total, weightKg)` instances.

#### Output
- `costFor` returns `BigDecimal` shipping cost or `0` for free shipping.
- Throws `ParcelTooHeavyException` when `order.weightKg > maxParcelKg`.

#### Core logic
- If `order.weightKg > maxParcelKg` → throw `ParcelTooHeavyException`.
- If `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` → return `0`.
- Define `tiers` list of maps: `upTo`, `price`.
- Find first tier where `order.weightKg <= tier.upTo` and return its `price` cast to `BigDecimal`.

#### Public API
- `ShippingRules.costFor(Order order) -> BigDecimal` – compute shipping cost.

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `shop.shipping.Order` (assumed)
- `shop.shipping.ParcelTooHeavyException` (assumed)

#### Exceptions / Errors
- `ParcelTooHeavyException` thrown for overweight parcels.
- No explicit null checks; relies on Groovy null safety.

#### Concurrency
- No threading or synchronization. All operations are stateless per instance.
