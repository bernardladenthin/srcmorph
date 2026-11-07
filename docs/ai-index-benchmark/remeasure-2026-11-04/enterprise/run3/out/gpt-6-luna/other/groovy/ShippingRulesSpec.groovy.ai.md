### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T08:19:52Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Calculates parcel shipping charges by order value and weight, providing free shipping above a threshold and rejecting parcels over the carrier’s weight limit.

#### Purpose
- `ShippingRulesSpec` verifies free-shipping eligibility, weight-based prices, and rejection of overweight parcels.
- `ShippingRules` computes shipping cost using order total and parcel weight.

#### Type
- `ShippingRulesSpec` — class; extends `Specification`.
- `ShippingRules` — class; `@CompileStatic`; no explicit superclass or interfaces.

#### Input
- `ShippingRules` properties: `carrier: String`, `maxParcelKg: BigDecimal`.
- `costFor(Order order)` consumes an `Order` with `weightKg` and `total`.
- `ShippingRulesSpec` initializes rules with carrier `DHL` and `maxParcelKg: 31.5`; tests construct orders with totals and weights.

#### Output
- `costFor(Order)` returns `BigDecimal` shipping cost.
- `costFor(Order)` throws `ParcelTooHeavyException` when the order exceeds `maxParcelKg`.
- `ShippingRulesSpec` assertions verify zero cost, tier prices `4.99`, `9.99`, `19.99`, and overweight rejection.

#### Core logic
- `costFor` first rejects parcels whose `weightKg` exceeds `maxParcelKg`, with a message containing actual and maximum weight.
- If `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` (`50.00`), returns `0`.
- Otherwise selects the first matching weight tier: up to 2 kg costs `4.99`; up to 10 kg costs `9.99`; up to 31.5 kg costs `19.99`.
- `ShippingRulesSpec` includes a data-driven `@Unroll` feature for the three paid tiers and separate free-shipping and overweight tests.

#### Public API
- `ShippingRulesSpec`:
  - `getRules()` / `setRules(ShippingRules)` -> rules property
  - `"orders above the threshold ship for free"()` -> verifies free shipping
  - `"a parcel of #weight kg costs #expected"()` -> verifies tier prices
  - `"an overweight parcel is rejected"()` -> verifies overweight rejection
- `ShippingRules`:
  - `getCarrier()` / `setCarrier(String)` -> carrier property
  - `getMaxParcelKg()` / `setMaxParcelKg(BigDecimal)` -> parcel weight limit
  - `costFor(Order)` -> calculate shipping cost

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `Order`
- `ParcelTooHeavyException`

#### Exceptions / Errors
- Throws `ParcelTooHeavyException` when `order.weightKg > maxParcelKg`.
- No explicit null handling for `order` or its properties.
