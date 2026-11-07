### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:26:05Z
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
- Provides unit tests for shipping rules.

#### Type
- `class ShippingRulesSpec` – test class extending `spock.lang.Specification`.
- `class ShippingRules` – shipping rule engine, `@CompileStatic`.

#### Input
- `ShippingRules` constructor: `carrier: String`, `maxParcelKg: BigDecimal`.
- `ShippingRules.costFor(Order order)` – `Order` with `total: BigDecimal`, `weightKg: BigDecimal`.

#### Output
- `ShippingRules.costFor` returns `BigDecimal` cost or `0` for free shipping.
- Throws `ParcelTooHeavyException` when `order.weightKg > maxParcelKg`.

#### Core logic
- If `order.weightKg > maxParcelKg` → throw `ParcelTooHeavyException`.
- If `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` → return `0`.
- Define `tiers` list: `[[upTo: 2, price: 4.99], [upTo: 10, price: 9.99], [upTo: 31.5, price: 19.99]]`.
- Find first tier where `order.weightKg <= tier.upTo` and return `tier.price` as `BigDecimal`.

#### Public API
- `ShippingRulesSpec."orders above the threshold ship for free"() -> void` – test.
- `ShippingRulesSpec."a parcel of #weight kg costs #expected"() -> void` – parameterized test.
- `ShippingRulesSpec."an overweight parcel is rejected"() -> void` – test.
- `ShippingRulesSpec.heavyOrder(BigDecimal kg) -> Order` – private helper.
- `ShippingRules.costFor(Order order) -> BigDecimal` – public method.

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `Order` (external)
- `ParcelTooHeavyException` (external)

#### Exceptions / Errors
- `ParcelTooHeavyException` thrown for overweight parcels.
- No explicit null checks; relies on Groovy null safety.

#### Concurrency
- None. All operations are synchronous and stateless.
