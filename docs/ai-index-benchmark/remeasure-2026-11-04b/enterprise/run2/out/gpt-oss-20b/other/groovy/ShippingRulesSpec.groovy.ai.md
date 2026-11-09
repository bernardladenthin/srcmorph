### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:32:45Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Calculates shipping cost based on order weight and total, with free shipping over a threshold and heavy‑parcel rejection.

#### Purpose
- Implements shipping cost calculation with weight tiers and free‑shipping threshold.
- Provides unit tests for the shipping logic.

#### Type
- `class ShippingRulesSpec` – Spock specification, extends `Specification`.
- `class ShippingRules` – Groovy class, `@CompileStatic`, properties `carrier: String`, `maxParcelKg: BigDecimal`.

#### Input
- `ShippingRules` constructor: `carrier`, `maxParcelKg`.
- `costFor(Order order)` parameters: `Order` with `total: BigDecimal`, `weightKg: BigDecimal`.
- Test data: `weight` and `expected` values in `where` block.

#### Output
- `costFor` returns `BigDecimal` cost or `0` for free shipping.
- Throws `ParcelTooHeavyException` when `order.weightKg > maxParcelKg`.

#### Core logic
- `costFor`:
  1. If `order.weightKg > maxParcelKg` → `throw ParcelTooHeavyException`.
  2. If `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` → `return 0`.
  3. Define `tiers = [[upTo: 2, price: 4.99], [upTo: 10, price: 9.99], [upTo: 31.5, price: 19.99]]`.
  4. Find first tier where `order.weightKg <= tier.upTo` and return `tier.price` as `BigDecimal`.

#### Public API
- `costFor(order: Order) -> BigDecimal` – compute shipping cost.

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `shop.shipping.Order` (assumed)
- `shop.shipping.ParcelTooHeavyException` (assumed)

#### Exceptions / Errors
- Throws `ParcelTooHeavyException` for overweight parcels.
- No other explicit exceptions.

#### Concurrency
- No concurrency constructs; single‑threaded logic.
